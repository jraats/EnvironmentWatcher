 /** 
   ******************************************************************************
   * @ File name:	main.c
   * @ Creator:		Jerry
   * @ Version:		V1.0.0
   * @ Date			26-January-2016
   * @ Functional Description: 	- Receives the data transmitted by the Raspberry 
								   Pi over TWI
   ******************************************************************************
 */
 
#define F_CPU 2000000UL
/* Includes ------------------------------------------------------------------*/
#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdio.h>
#include <util/delay.h>


/* Private define ------------------------------------------------------------*/
#define SLAVE_ADDR		0x01
#define BAUD_100K		9600 

#define ENABLE_UART_C1	1
#define C1_BAUD         9600
#define C1_CLK2X        0

#define Temp			1
#define Licht			2

#define VCC				3.30
#define VREF			(((double) VCC) / 1.6) // 2.06125
#define VOFFSET			((0.05)*(VREF)) // 0.103125
#define MAX_VALUE		4095

/* Private variables ---------------------------------------------------------*/
volatile uint8_t flag = 0;
volatile uint8_t c = 0; 

static volatile uint16_t res;
static volatile double	volt; 

volatile double degC, intTemp;
volatile unsigned int ADC_temp;


#include "twi_slave_driver.h"
#include "avr_compiler.h"
#include "uart.h"

TWI_Slave_t twis;
volatile uint8_t d[TWIS_RECEIVE_BUFFER_SIZE]; 
/* Private function prototypes -----------------------------------------------*/
void init_adc(void);
uint16_t read_adc(void);
uint16_t read_adc1(void);
double read_temp(void);

/* Private functions ---------------------------------------------------------*/
/*
 * @brief  If there is new data received over the TWI, data will be stored in buffer and flag will be one.
 * @param  None.
 * @retval None.
 */
void SlaveReceiveData(void){
	uint8_t index = twis.bytesReceived;
	d[index]      = twis.receivedData[index];
	if (d[0]==1){
		flag= Temp;
	}
	else{
		flag= Licht;
	}
} 

/*
 * @brief  Send UART.
 * @param  None.
 * @retval Unsigned 16bit int.
 */int uart_fputc(char c, FILE *stream){
	uart_putc(&uartC1, c);
	return 0;
}

FILE uart_stdinout = FDEV_SETUP_STREAM(uart_fputc, NULL, _FDEV_SETUP_WRITE);

/**
  * @brief  Main program.
  * @param  None
  * @retval None
  */
int main(void){
			init_adc();
	init_uart(&uartC1, &USARTC1, F_CPU, C1_BAUD, C1_CLK2X);
	
	stdout = &uart_stdinout;
	
	TWI_SlaveInitializeDriver(&twis, &TWIC,SlaveReceiveData);
	TWI_SlaveInitializeModule(&twis, SLAVE_ADDR, TWI_SLAVE_INTLVL_LO_gc);
	PMIC.CTRL |= PMIC_LOLVLEN_bm;	sei();

	PORTD.DIRSET = PIN0_bm;

	while(1) {		
		//PORTD.OUTTGL = PIN0_bm;
		if( flag == Temp)			
		{
			PORTD.OUTSET = PIN0_bm;
			res = read_adc1();
			degC = read_temp();
			printf("%4d\n", (int)degC);
			twis.sendData[0] = (int)degC; 
			flag = 0;
			
		}
		else if(flag == Licht){
			PORTD.OUTCLR = PIN0_bm;
			res = read_adc1();
			printf("res:%d\n", res);
			twis.sendData[0] = res;
			flag = 0;
		}
				
	}	
}

/*
 * @brief  Interrupt Service Routine.
 * @param  None.
 * @retval None.
 */
ISR(TWIC_TWIS_vect){
	TWI_SlaveInterruptHandler(&twis);
} 

/*
 * @brief  Initialize ADC.
 * @param  None.
 * @retval None.
 */
void init_adc(void){
	PORTA.DIRCLR		= PIN2_bm|PIN3_bm; // configure PA2 as input
	ADCA.CH0.MUXCTRL	= ADC_CH_MUXPOS_PIN1_gc; // PA1 to channel 0
	ADCA.CH1.MUXCTRL	= ADC_CH_MUXPOS_PIN2_gc; // PA2 to channel 1
	ADCA.CH3.MUXCTRL	= ADC_CH_MUXINT_TEMP_gc; // Set to internal temperature input
	ADCA.CH0.CTRL		= ADC_CH_INPUTMODE_SINGLEENDED_gc; // channel 0 single ended
	ADCA.CH1.CTRL		= ADC_CH_INPUTMODE_SINGLEENDED_gc; // channel 1 single ended
	ADCA.CH3.CTRL		= ADC_CH_GAIN_1X_gc | ADC_CH_INPUTMODE_INTERNAL_gc;
	ADCA.REFCTRL		= ADC_REFSEL_INTVCC_gc | ADC_TEMPREF_bm; // internal VCC/1.6 reference
	ADCA.CTRLB			= ADC_RESOLUTION_12BIT_gc; // 12 bits conversion, ...
	ADCA.PRESCALER		= ADC_PRESCALER_DIV16_gc; // 2MHz/16 = 125kHz
	ADCA.CTRLA			= ADC_ENABLE_bm; // enable adc
} /*
 * @brief  Read ADC channel 0.
 * @param  None.
 * @retval Unsigned 16bit int.
 */uint16_t read_adc(void){
		
	ADCA.CH0.CTRL	 |= ADC_CH_START_bm; // start ADC conversion
	while ( !(ADCA.CH0.INTFLAGS & ADC_CH_CHIF_bm) ) ; // wait until it’s ready
	res				  = ADCA.CH0.RES;
	ADCA.CH0.INTFLAGS|= ADC_CH_CHIF_bm; // reset interrupt flag
	return res; // return measured value
} /*
 * @brief  Read ADC channel 1.
 * @param  None.
 * @retval Unsigned 16bit int.
 */uint16_t read_adc1(void){
	
	ADCA.CH1.CTRL	 |= ADC_CH_START_bm; // start ADC conversion
	while ( !(ADCA.CH1.INTFLAGS & ADC_CH_CHIF_bm) ) ; // wait until it’s ready
	res				  = ADCA.CH1.RES;
	ADCA.CH1.INTFLAGS|= ADC_CH_CHIF_bm; // reset interrupt flag
	return res; // return measured value
} /*
 * @brief  Read Internal Temperature sensor.
 * @param  None.
 * @retval double.
 */double read_temp(void){
	
	ADCA.CH3.CTRL	 |= ADC_CH_START_bm; // start single ADC conversion
	while ( !(ADCA.CH3.INTFLAGS & ADC_CH_CHIF_bm) ) ; // wait for conversion done, CHIF flag active
	ADC_temp		  = ADCA.CH3.RES;
	ADCA.CH3.INTFLAGS|= ADC_CH_CHIF_bm; // reset interrupt flag
	volt = ( ((double) ADC_temp) * VREF / (MAX_VALUE + 1) ) - VOFFSET;
	intTemp = (((volt*VCC)/MAX_VALUE)*100000)-15;
	
	return intTemp; // return measured value
} /******************* (C) COPYRIGHT 2016 Enviroment Watcher *****END OF FILE****/
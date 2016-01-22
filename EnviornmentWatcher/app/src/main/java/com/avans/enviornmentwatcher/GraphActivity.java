package com.avans.enviornmentwatcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that displays the graphs
 */
public class GraphActivity extends AppCompatActivity {
    GraphView graph;                                    //holds the graph
    CalendarView calenderView;                          //holds the calender
    LineGraphSeries<DataPoint> mSeries1;                //holds the datapoints that fill the graph
    TextView text_Sensor_Item;                          //Displays if your watching Light or Temperature
    String item;                                        //Holds the choice of watching light of Temperature

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        text_Sensor_Item = (TextView) findViewById(R.id.text_Sensor_Item);

        //Get items sent from previous screen
        //Check if the graph shows light or temperature
        Bundle extra = getIntent().getExtras();
        if(extra.get("item").equals("light")){
            text_Sensor_Item.setText(getResources().getString(R.string.text_Item_Light));
            item = "light";

        }
        else{
            text_Sensor_Item.setText(getResources().getString(R.string.text_Item_Temperature));
            item = "temperature";
        }

        //setup graph
        graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);

        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-20);
        graph.getViewport().setMaxY(40);

        graph.getGridLabelRenderer().setNumHorizontalLabels(6);
        //graph.getGridLabelRenderer().setNumVerticalLabels(10);

        //gets points for graph, when user selects a date
        calenderView = (CalendarView) findViewById(R.id.calendarView);
        calenderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // create storage with points for graph to draw
                // Reset so old data is removed
                graph.removeAllSeries();
                mSeries1 = new LineGraphSeries<DataPoint>();
                graph.addSeries(mSeries1);

                //Resets the viewport so it doesn't crash when the value is less then the previous values
                graph.getViewport().scrollToEnd();

                System.out.println("Going to generate Data");

                //create Data for Graph
                String date = year+"-"+(month+1)+"-"+dayOfMonth;
                generateData(item, date);
            }
        });
    }

    /*! \Get data
     *  @param item must be temperature or light (what user would like to see)
     *  @param date from wich day the person wants to see it
     *  Gets the Data from the server based on a date
     */
    private void generateData(final String item, final String date) {
        JSONCommunicator.getInstance().getSensorDataWithDate(date, date, 2, new CommunicationInterface<ArrayList<HashMap<String, String>>>() {
            @Override
            public void getResponse(ArrayList<HashMap<String, String>> object) {
                int meanValue = 1;
                int index = 0;
                int time = 0;
                ArrayList<Double> data = new ArrayList<Double>();
                ArrayList<DataPoint> values = new ArrayList<DataPoint>();

                //normalize if there are more then 1500 values
                //Graphview crashes otherwise
                if(object.size() > 1500)
                    meanValue = object.size() / 1000;

                for (int i = 0; i < object.size(); i++) {
                    System.out.println(item);

                    if(item.equals("temperature"))
                        data.add(Double.parseDouble(object.get(i).get("temperature")));
                    else
                        data.add(Double.parseDouble(object.get(i).get("light")));

                    if(i == (index+meanValue)-1)
                    {
                        double value = 0;
                        for(int ii = 0; ii < data.size(); ii++)
                        {
                            value = value + data.get(ii);
                        }

                        value = (value/data.size());
                        int pp= index+meanValue-1;
                        mSeries1.appendData(new DataPoint(time, value), true, 40);

                        index=i+1;
                        time = time+1;
                        data = new ArrayList<>();
                    }
                }
            }
        });
    }
}


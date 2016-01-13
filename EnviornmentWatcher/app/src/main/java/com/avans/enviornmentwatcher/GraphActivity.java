package com.avans.enviornmentwatcher;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphActivity extends AppCompatActivity {
    GraphView graph;
    CalendarView calenderView;
    LineGraphSeries<DataPoint> mSeries1;
    String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        //Get items sent from previous screen
        //Check if the graph shows light or temperature
        Bundle extra = getIntent().getExtras();
        if(extra.get("item").equals("light"))
            item = "light";
        else
            item = "temperature";


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


    //Gets the Data from the server based on a date
    private void generateData(final String item, final String date) {
        JSONCommunicator.getInstance().getSensorDataWithDate(date, date, 2, new CommunicationInterface<ArrayList<HashMap<String, String>>>() {
            @Override
            public void getResponse(ArrayList<HashMap<String, String>> object) {
                int meanValue = 1;
                int index = 0;
                int time = 0;
                ArrayList<Integer> data = new ArrayList<Integer>();
                ArrayList<DataPoint> values = new ArrayList<DataPoint>();

                //normalize if there are more then 1000 values
                //Graphview crashes otherwise
                if(object.size() > 1000)
                    meanValue = object.size() / 1000;

                System.out.println(meanValue);


                for (int i = 0; i < object.size(); i++) {
                    if(item.equals("temperature"))
                        data.add(Integer.valueOf(object.get(i).get("temperature")));
                    else
                        data.add(Integer.valueOf(object.get(i).get("light")));

                    if(i == (index+meanValue)-1)
                    {
                        int value = 0;
                        for(int ii = 0; ii < data.size(); ii++)
                        {
                            value = value + data.get(ii);
                        }

                        value = (value/data.size());
                        int pp= index+meanValue-1;
                        System.out.println(pp);
                        mSeries1.appendData(new DataPoint(time, value), true, 40);

                        index=i+1;
                        time = time+1;
                        data = new ArrayList<Integer>();
                    }
                }
            }
        });
    }
}


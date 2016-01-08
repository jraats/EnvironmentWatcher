package com.avans.enviornmentwatcher;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphActivity extends AppCompatActivity {
    GraphView graph;
    LineGraphSeries<DataPoint> mSeries1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);

        mSeries1 = new LineGraphSeries<DataPoint>();
        graph.addSeries(mSeries1);
        generateData("temperature");
        System.out.println("Going to continue");

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


    }

    private void generateData(final String s) {
        JSONCommunicator.getInstance().getSensorDataWithDate("2016-01-05", "2016-01-05", 2, new CommunicationInterface<ArrayList<HashMap<String, String>>>() {
            @Override
            public void getResponse(ArrayList<HashMap<String, String>> object) {
                int meanValue = 1;
                int index = 0;
                ArrayList<Integer> data = new ArrayList<Integer>();
                ArrayList<DataPoint> values = new ArrayList<DataPoint>();

                if(object.size() > 2)
                    meanValue = object.size() / 2;

                for (int i = 0; i < object.size(); i++) {
                    if(s.equals("temperature"))
                        data.add(Integer.valueOf(object.get(i).get("temperature")));
                    else
                        data.add(Integer.valueOf(object.get(i).get("light")));

                    if(i == index+meanValue)
                    {
                        int value = 0;
                        for(int ii = 0; ii < data.size(); ii++)
                        {
                            value = value + data.get(ii);
                            System.out.println("Counting " + ii);

                        }

                        value = (value/data.size());
                        mSeries1.appendData(new DataPoint(i, value), true, 40);


                        index=i;
                        data = new ArrayList<Integer>();
                    }
                }
            }
        });
    }
}

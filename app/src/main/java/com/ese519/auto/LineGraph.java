package com.ese519.auto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by jonathanfields on 11/30/15.
 */
public class LineGraph {

    // fake data
    //int [] x = {1, 2, 3, 4, 5, 6, 7, 8};
    //int [] y = {10, 20, 30, 40, 50, 60, 70, 80};

    private TimeSeries dataset = new TimeSeries("UO");
    private XYSeriesRenderer renderer = new XYSeriesRenderer();
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    public LineGraph() {

        // add dataset to mult dataset
        mDataset.addSeries(dataset);

        // customization for series
        renderer.setColor(Color.RED);
        renderer.setFillPoints(true);
        renderer.setDisplayBoundingPoints(false);
        renderer.setFillPoints(true);
        renderer.setLineWidth(2f);
        renderer.setPointStyle(PointStyle.SQUARE);
        renderer.setFillPoints(true);

        // customize graph
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setShowGrid(true);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.LTGRAY);
        mRenderer.setGridColor(Color.WHITE);
        mRenderer.setLabelsTextSize(50f);
        mRenderer.setXLabels(5);
        mRenderer.setYLabels(5);
        mRenderer.setMargins(new int[]{150, 150, 150, 0});
        mRenderer.setYLabelsVerticalPadding(-5f);
        mRenderer.setYLabelsPadding(40);
        mRenderer.setXTitle("Time (seconds ago)");
        mRenderer.setYTitle("UO (mL/hr)");
        mRenderer.setChartTitle("Patient XYZ UO (ml/hr)");
        mRenderer.setChartTitleTextSize(50f);

        // add to mult renderer
        mRenderer.addSeriesRenderer(renderer);
    }

    public GraphicalView getView(Context context) {
        return ChartFactory.getLineChartView(context, mDataset, mRenderer);
    }

    public void addPoint(Point P) {
        dataset.add(P.getX(), P.getY());
    }

    public void clearLine() {
        dataset.clear();
    }

    /*
    public Intent getIntent(Context context) {

        // fake series
        TimeSeries series = new TimeSeries("Line1");
        for (int i=0;i<x.length;i++) {
            series.add(x[i],y[i]);
        }

        // fake dataset
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        // fake renderer
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(Color.RED);
        renderer.setFillPoints(true);
        renderer.setDisplayBoundingPoints(false);
        renderer.setFillPoints(true);
        renderer.setLineWidth(2f);
        renderer.setPointStyle(PointStyle.SQUARE);
        renderer.setFillPoints(true);

        // fake mult series renderer
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.setShowGrid(true);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.LTGRAY);
        mRenderer.setGridColor(Color.WHITE);
        mRenderer.setLabelsTextSize(50f);
        mRenderer.setXLabels(5);
        mRenderer.setYLabels(5);
        mRenderer.setMargins(new int[]{150, 150, 150, 0});
        mRenderer.setYLabelsVerticalPadding(-5f);
        mRenderer.setYLabelsPadding(40);
        mRenderer.setXTitle("Time (minutes ago)");
        mRenderer.setYTitle("UO (mL/hr)");
        mRenderer.setChartTitle("Patient XYZ UO (ml/hr)");
        mRenderer.setChartTitleTextSize(50f);
        mRenderer.addSeriesRenderer(renderer);


        // fake intent
        Intent intent = ChartFactory.getLineChartIntent(context, dataset, mRenderer, "Line Graph JF");
        return intent;

    }
    */
}

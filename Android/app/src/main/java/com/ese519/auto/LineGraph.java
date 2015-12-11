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

    private TimeSeries dataset_rate = new TimeSeries("UO_hourly");
    private TimeSeries dataset_level = new TimeSeries("UO_level");
    private XYSeriesRenderer renderer_rate = new XYSeriesRenderer();
    private XYSeriesRenderer renderer_level = new XYSeriesRenderer();
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    public LineGraph() {

        // add dataset to mult dataset
        mDataset.addSeries(dataset_rate);
        mDataset.addSeries(dataset_level);

        // customization for level series
        renderer_level.setColor(Color.BLUE);
        renderer_level.setFillPoints(true);
        renderer_level.setDisplayBoundingPoints(false);
        renderer_level.setFillPoints(true);
        renderer_level.setLineWidth(2f);
        renderer_level.setPointStyle(PointStyle.SQUARE);
        renderer_level.setFillPoints(true);

        // customization for hourly series
        renderer_rate.setColor(Color.RED);
        renderer_rate.setFillPoints(true);
        renderer_rate.setDisplayBoundingPoints(false);
        renderer_rate.setFillPoints(true);
        renderer_rate.setLineWidth(2f);
        renderer_rate.setPointStyle(PointStyle.SQUARE);
        renderer_rate.setFillPoints(true);

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
        mRenderer.setChartTitle("Patient XYZ UO");
        mRenderer.setChartTitleTextSize(50f);

        // add to mult renderer
        mRenderer.addSeriesRenderer(renderer_rate);
        mRenderer.addSeriesRenderer(renderer_level);
    }

    public GraphicalView getView(Context context) {
        return ChartFactory.getLineChartView(context, mDataset, mRenderer);
    }

    public void addPoint_rate(Point P) {
        dataset_rate.add(P.getX(), P.getY());
    }

    public void addPoint_level(Point P) {
        dataset_level.add(P.getX(), P.getY());
    }

    public void clearLine() {
        dataset_rate.clear();
        dataset_level.clear();
    }

}

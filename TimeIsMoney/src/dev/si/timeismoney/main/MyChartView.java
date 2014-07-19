package dev.si.timeismoney.main;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.axis.CategoryAxis;
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.axis.ValueAxis;
import org.afree.chart.plot.CategoryPlot;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.renderer.category.CategoryItemRenderer;
import org.afree.data.category.CategoryDataset;
import org.afree.data.category.DefaultCategoryDataset;
import org.afree.graphics.PaintType;
import org.afree.graphics.SolidColor;
import org.afree.graphics.geom.Font;

import dev.si.timeismoney.main.AppDetailActivity.Type;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyChartView extends ChartView {
	
	public MyChartView(Context context, AttributeSet attr) {
		super(context, attr);
	}
	
	public void createChart(int[] dataList, Type type) {
		
        // データのセット
        CategoryDataset data = setDataset(dataList, type);
        
        AFreeChart chart = ChartFactory.createLineChart("title", type.toString(), "minute", data, PlotOrientation.VERTICAL
        		, false, false, false);
        
	    // 折れ線の定義
        CategoryPlot plot = chart.getCategoryPlot();
        
        setAxisSetting(plot.getDomainAxis(), plot.getRangeAxis());
        
        CategoryItemRenderer renderer = plot.getRenderer();
        
        // 太さ
        renderer.setSeriesStroke(0, 10f);
        
        // 色
        if (type == Type.WEEK) 
        	renderer.setSeriesPaintType(0, new SolidColor(Color.rgb(166, 227, 157)));
        else
        	renderer.setSeriesPaintType(0, new SolidColor(Color.rgb(106, 140, 199)));
        
        AFreeChart aFreeChart = new AFreeChart(plot);
        // 最大描写領域を設定
        setMaximumDrawWidth(800);
        setMaximumDrawHeight(800);
        
        PaintType background = new SolidColor(Color.rgb(255, 255, 255));
        aFreeChart.setBackgroundPaintType(background);
        setChart(aFreeChart);
    }
	
	private CategoryDataset setDataset(int[] dataList, Type type) {
		DefaultCategoryDataset data = new DefaultCategoryDataset();
		int i = 1;
		if(type == Type.WEEK) {
	        for (int value : dataList) {
	            data.addValue(value, "", int2Week(i));
	            i++;
	        }
		} else {
			for (int value : dataList) {
				data.addValue(value, "", String.valueOf(i-1));
				i++;
			}
		}
        return data;
	}
	
	private String int2Week(int week) {
		String str = "";
		switch (week) {
		case 1:
			str = "日";
			break;
		case 2:
			str = "月";
			break;
		case 3:
			str = "火";
			break;
		case 4:
			str = "水";
			break;
		case 5:
			str = "木";
			break;
		case 6:
			str = "金";
			break;
		case 7:
			str = "土";
		default:
			break;
		}
		return str;
	}
	
	private void setAxisSetting(CategoryAxis domainAxis, ValueAxis rangeAxis) {
		
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// XY軸のタイトルのフォント設定
		// 種類、大きさ
		Font xyTitleFont = new Font(Typeface.SANS_SERIF, Typeface.BOLD, 30);
		domainAxis.setLabelFont(xyTitleFont);
		rangeAxis.setLabelFont(xyTitleFont);

		// 色
		PaintType xyTitleColor = new SolidColor(Color.rgb(100, 100, 100));
		domainAxis.setLabelPaintType(xyTitleColor);
		rangeAxis.setLabelPaintType(xyTitleColor);

		// XY軸の線の設定
		// 太さ
		domainAxis.setAxisLineStroke(5);
		rangeAxis.setAxisLineStroke(5);
		// 色
		PaintType xyLineColor = new SolidColor(Color.rgb(180, 180, 180));
		domainAxis.setAxisLinePaintType(xyLineColor);
		rangeAxis.setAxisLinePaintType(xyLineColor);
		// 非表示
		domainAxis.setAxisLineVisible(false);
		rangeAxis.setAxisLineVisible(false);

		// XY軸の目盛のフォントの設定
		// フォントの種類、大きさ
		Font tickFont = new Font(Typeface.MONOSPACE, Typeface.NORMAL, 20);
		domainAxis.setTickLabelFont(tickFont);
		rangeAxis.setTickLabelFont(tickFont);
		// 非表示
		// domainAxis.setTickLabelsVisible(false);
		// rangeAxis.setTickLabelsVisible(false);

		// XY軸の目盛のマークの設定
		// 外への長さ
		domainAxis.setTickMarkOutsideLength(0);
		rangeAxis.setTickMarkOutsideLength(0);
		// 中への長さ
		domainAxis.setTickMarkInsideLength(10);
		rangeAxis.setTickMarkInsideLength(10);
		// 太さ
		domainAxis.setTickMarkStroke(5);
		rangeAxis.setTickMarkStroke(5);
		// 色
		PaintType xyMarkColor = new SolidColor(Color.rgb(180, 180, 180));
		domainAxis.setTickMarkPaintType(xyMarkColor);
		rangeAxis.setTickMarkPaintType(xyMarkColor);

	}

}

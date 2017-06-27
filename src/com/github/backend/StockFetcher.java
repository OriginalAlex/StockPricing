package com.github.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.display.Controller;

public class StockFetcher {

	private Controller controller;
	public String ticker;
	private Map<String, Double> prices = new LinkedHashMap<String, Double>();

	public StockFetcher(String ticker, String name) {
		this.controller = Controller.getInstance();
		this.ticker = ticker;
	}
	
	public Map<String, Double> fetchPrices() {
		return this.prices;
	}

	public void addPrices(String startDaysAgo, String rowCount) {
		Document doc;
		try {
			doc = Jsoup.connect("https://www.google.com/finance/historical?q=" + ticker + "&start=" + startDaysAgo
					+ "&num=" + rowCount).get();
			Elements dates = doc.select("tr");
			Elements stockClosing = doc.select("td.rgt");
			List<String> allDates = new ArrayList<String>();
			int index = 0;
			for (Element e : dates) {
				Elements date = e.select("td.lm");
				if (!date.text().trim().equals("")) {
					allDates.add(date.text());
				}
			}
			int count = 0;
			for (Element e : stockClosing) { // we want the closing price for
												// each day
				count %= 5;
				if (count == 3) {
					prices.put(allDates.get(index++), Double.parseDouble(e.text().replaceAll(",", "")));
				}
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getSigChanges(double percentChange) {
		List<String> keys = new ArrayList<String>(prices.keySet());
		double lastPrice = prices.get(keys.get(keys.size()-1)); // Since we can't know the value TOMORROW initially set it to today's value (0% change)
		for (int i = keys.size()-1; i > -1; i--) {
			String date = keys.get(i);
			double price = prices.get(date);
			double percentDiff = ((price * 100) / lastPrice) - 100;
			percentDiff = Math.round(percentDiff * 100.0) / 100.0;
			if (i != keys.size()-1 && (percentDiff > percentChange || -percentChange > percentDiff)) {
				controller.addRow(keys.get(i+1), date, percentDiff);
			}
			lastPrice = price;
		}
	}

}

package com.github.display;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.backend.Main;
import com.github.backend.PriceMovement;
import com.github.backend.StockFetcher;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class Controller {

	@FXML
	private LineChart<Number, Number> chart;

	@FXML
	private TableView<PriceMovement> table;

	@FXML
	private TableColumn<PriceMovement, String> fromColumn;

	@FXML
	private TableColumn<PriceMovement, String> toColumn;

	@FXML
	private TableColumn<PriceMovement, Double> percentChangeColumn;

	@FXML
	private TextArea tickerArea;

	@FXML
	private TextArea name;

	@FXML
	private Text display;

	private static Controller instance;
	private double tempVal;
	private Map<StackPane, Object[]> hovers;
	private String ticker = "GOOG";
	private String companyName = "google";
	private int numberOfEntries = 365;

	public static Controller getInstance() {
		return instance;
	}

	private void initializeTable() {
		this.fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
		this.toColumn.setCellValueFactory(new PropertyValueFactory<>("to"));
		this.percentChangeColumn.setCellValueFactory(new PropertyValueFactory<>("percentChange"));
	}

	public void addRow(String from, String to, double percentChange) {
		Platform.runLater(() -> {
			PriceMovement pm = new PriceMovement(from, to, percentChange);
			table.getItems().add(pm);
			ContextMenu cm = new ContextMenu();
			MenuItem search = new MenuItem("Google Search");
			search.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					try {
						ObservableList<PriceMovement> i = table.getSelectionModel().getSelectedItems();
						Desktop.getDesktop().browse(
								new URL(("https://www.google.co.uk/search?q=" + i.get(0).getTo().replaceAll(",", "")
										+ " " + companyName).replaceAll(" ", "+")).toURI());
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			});
			cm.getItems().add(search);
			table.setContextMenu(cm);
		});
	}

	private StackPane createHover() {
		StackPane hover = new StackPane();
		hover.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				Label priceIndicator = new Label(hovers.get(hover)[1] + ": $" + String.valueOf(hovers.get(hover)[0]));
				priceIndicator.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
				priceIndicator.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
				priceIndicator.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
				hover.getChildren().add(priceIndicator);
				hover.setCursor(Cursor.NONE);
				hover.toFront();
				hover.setOpacity(1.0);
			}
		});
		hover.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				hover.getChildren().clear();
				hover.setCursor(Cursor.CROSSHAIR);
				hover.setOpacity(0.0);
			}
		});
		ContextMenu cm = new ContextMenu();
		MenuItem google = new MenuItem("Google Search");
		google.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				try {
					Desktop.getDesktop()
							.browse(new URL(("https://www.google.co.uk/search?q="
									+ ((String) hovers.get(hover)[1]).replaceAll(",", "") + " " + companyName)
											.replaceAll(" ", "+")).toURI());
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		cm.getItems().add(google);
		hover.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.isSecondaryButtonDown()) {
					cm.show(hover, e.getScreenX(), e.getScreenY());
				}
			}
		});
		return hover;
	}

	private int numberOfDaysDifference(String date1, String date2) {
		int date1Val = getDayValue(date1);
		int date2Val = getDayValue(date2);
		String year1 = date1.split(" ")[2];
		String year2 = date2.split(" ")[2];
		return (date2Val - date1Val) + 365 * (Integer.parseInt(year2) - Integer.parseInt(year1));
	}

	private int getDayValue(String date) { // all follow the format: MON(th) DAY
		String[] parts = date.split(" ");
		String month = parts[0];
		int val = Integer.parseInt(parts[1].replaceAll(",", ""));
		switch (month) {
		case ("Jan"):
			val += 31;
			break;
		case ("Feb"):
			val += 59;
			break;
		case ("Mar"):
			val += 89;
			break;
		case ("Apr"):
			val += 119;
			break;
		case ("May"):
			val += 150;
			break;
		case ("Jun"):
			val += 180;
			break;
		case ("Jul"):
			val += 211;
			break;
		case ("Aug"):
			val += 242;
			break;
		case ("Sep"):
			val += 272;
			break;
		case ("Oct"):
			val += 303;
			break;
		case ("Nov"):
			val += 334;
			break;
		case ("Dec"):
			val += 365;
			break;
		}
		return val;
	}
	
	private void getStockPrices(StockFetcher sf) { // number of entries variable stores the number of quotes to collect.
		int current = 0;
		while (numberOfEntries - 200 > 0) {
			sf.addPrices(String.valueOf(current), "200");
			current += 200;
			numberOfEntries -= 200;
		}
		sf.addPrices(String.valueOf(current), String.valueOf(numberOfEntries));
	}

	private void updateChart(LineChart<Number, Number> chart) {
		table.getItems().clear();
		hovers = new HashMap<StackPane, Object[]>();
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		StockFetcher sf = new StockFetcher(ticker, companyName);
		getStockPrices(sf);
		chart.getXAxis().setLabel("Time");
		chart.getYAxis().setLabel("Value ($)");
		Map<String, Double> prices = sf.fetchPrices();
		List<String> keys = new ArrayList<String>(prices.keySet());
		int day = 0;
		for (int i = keys.size() - 1; i > -1; i--) {
			tempVal = prices.get(keys.get(i));
			if (i != 0) {
				XYChart.Data<Number, Number> value = new XYChart.Data<Number, Number>(
						(day += numberOfDaysDifference(keys.get(i), keys.get(i - 1))), tempVal);
				StackPane hover = createHover();
				hover.setOpacity(0.0);
				hovers.put(hover, new Object[] { tempVal, keys.get(i) });
				value.setNode(hover);
				series.getData().add(value);
			}
		}
		sf.getSigChanges(2);
		chart.getData().add(series);
	}
	
	private void updateLineChartWithTimestamp(int entries) {
		this.numberOfEntries = entries;
		LineChart<Number, Number> newChart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
		updateChart(newChart);
		Main.getInstance().setCentre(newChart);
	}
	
	@FXML
	private void setOneMonth() {
		updateLineChartWithTimestamp(31);
	}
	
	@FXML
	private void setOneYear() {
		updateLineChartWithTimestamp(365);
	}
	
	@FXML
	private void setSixMonths() {
		updateLineChartWithTimestamp(186);
	}
	
	@FXML
	private void setFiveYears() {
		updateLineChartWithTimestamp(1825);
	}

	@FXML
	private void updateCompany() {
		this.companyName = name.getText();
		this.ticker = tickerArea.getText();
		this.display.setText("Displaying: " + this.companyName);
		LineChart<Number, Number> newChart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
		updateChart(newChart);
		Main.getInstance().setCentre(newChart);
	}

	@FXML
	private void initialize() {
		instance = this;
		initializeTable();
		updateChart(chart);
	}

}

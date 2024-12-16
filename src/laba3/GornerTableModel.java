package laba3;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class GornerTableModel extends AbstractTableModel {

	private Double[] coefficients;
	private Double from;
	private Double to;
	private Double step;

	public GornerTableModel(Double from, Double to, Double step, Double[] coefficients) {
		this.from = from;
		this.to = to;
		this.step = step;
		this.coefficients = coefficients;
	}

	public Double getFrom() {
		return from;
	}

	public Double getTo() {
		return to;
	}

	public Double getStep() {
		return step;
	}

	public int getColumnCount() {
		return 4;
	}

	public int getRowCount() {
		return (int) (Math.ceil((to - from) / step)) + 1;
	}

	public Object getValueAt(int row, int col) {
		double x = from + step * row;
		switch(col) {
		default:
		case 0:
			return x;
		case 1:
			Double result = 0.0;
			for (int i = 0; i < coefficients.length; i++) {
			    result = result * x + coefficients[i];
			}

			return result;
		case 2:
		    Float floatResult = (float)0.0; // Используем тип float
		    for (int i = 0; i < coefficients.length; i++) {
		        floatResult =(float)( x * floatResult + coefficients[i ].floatValue());
		    }
		   
		    return floatResult;
		    

		case 3:
			result = 0.0;
			floatResult = (float)0.0;
			for (int i = 0; i < coefficients.length; i++) {
				result = x * result + coefficients[coefficients.length - i - 1];
			}
			for (int i = 0; i < coefficients.length; i++) {
				floatResult =(float)( x * floatResult + coefficients[coefficients.length - i - 1]);
			}
			return result - floatResult;
		}
		
	}

	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Значение X";
		default:
		case 1:
			return "Значение многочлена(double)";
		case 2:
			return "Значение многочлена(float)";
		case 3:
			return "Разность";
		}
	}

	public Class<?> getColumnClass(int col) {
		return Double.class;
	}
}


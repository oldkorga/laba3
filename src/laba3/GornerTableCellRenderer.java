package laba3;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class GornerTableCellRenderer implements TableCellRenderer {
    private JPanel panel = new JPanel();
    private JLabel label = new JLabel();
    private JCheckBox checkBox = new JCheckBox();

    private String needle = null;
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
    private boolean isPalindromeCheckEnabled = false; // Флаг для проверки палиндромов

    public GornerTableCellRenderer() {
        formatter.setMaximumFractionDigits(10);
        formatter.setGroupingUsed(false);
        DecimalFormatSymbols dottedDouble = formatter.getDecimalFormatSymbols();
        dottedDouble.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(dottedDouble);

        panel.add(label);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        checkBox.setEnabled(false); // Флажок только для отображения
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int col) {
        String formattedDouble = formatter.format(value);

        // Проверка на палиндром, если включена
        if (isPalindromeCheckEnabled && value != null) {
            String cleanedValue = formattedDouble.replaceAll("[^0-9]", "");
            String reversedValue = new StringBuilder(cleanedValue).reverse().toString();
            if (reversedValue.equals(cleanedValue) && col == 1) {
                panel.setBackground(Color.RED); // Задаем цвет фона для палиндромов
            } else {
                panel.setBackground(Color.WHITE); // Обычное поведение для других ячеек
            }
        } else {
            panel.setBackground(Color.WHITE); // Если проверка отключена
        }

        // Если значение совпадает с needle и находится в нужном столбце
        if (col == 1 && needle != null && needle.equals(formattedDouble)) {
            checkBox.setSelected(true); // Отметить флажок
            checkBox.setBackground(Color.YELLOW); // Установить фон флажка
            return checkBox; // Возвращаем флажок вместо панели
        }

        label.setText(formattedDouble);
        panel.add(label);
        return panel;
    }

    // Устанавливает значение для выделения
    public void setNeedle(String needle) {
        this.needle = needle;
    }

    // Включение или отключение проверки палиндромов
    public void setPalindromeCheckEnabled(boolean enabled) {
        this.isPalindromeCheckEnabled = enabled;
    }
}

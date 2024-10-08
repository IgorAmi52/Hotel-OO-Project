package com.service;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class ContainerService {
	
	public static int panelHeight = 650;
	public static int panelWidth = 1000;
	
	private static  Properties p = new Properties();
	
	public static void resetFields(Container container) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof JTextField) {
                ((JTextField) component).setText("");
            } else if (component instanceof JComboBox) {
                ((JComboBox<?>) component).setSelectedIndex(0);
            } else if (component instanceof JCheckBox) {
                ((JCheckBox) component).setSelected(false);
            } else if (component instanceof Container) {
                resetFields((Container) component); // Recursively reset fields in nested containers
            }
        }
    }
    public static String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null; // Return null if no button is selected
    }
    
    public static JDatePickerImpl getDatePicker() {
    	  if(p.isEmpty()) {
           p.put("text.today", "Today");
           p.put("text.month", "Month");
           p.put("text.year", "Year");
    	  }
    	  UtilDateModel model = new UtilDateModel();
          JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
          JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
           
          return datePicker;
    }
	public static String[] getSelectedValues(ArrayList<JCheckBox> addList) {
		  List<String> selectedValues = new ArrayList<>();
		  for (JCheckBox checkBox : addList) {
		    if (checkBox.isSelected()) {
		      selectedValues.add(checkBox.getText());
		    }
		  }
		  return selectedValues.toArray(new String[0]);
		}

	
}

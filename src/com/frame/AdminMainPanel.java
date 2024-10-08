package com.frame;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.frame.small.HomePanel;
import com.frame.small.ManagePricesPanel;
import com.frame.small.ManageServicesPanel;
import com.frame.small.ManageStaffPanel;
import com.frame.small.RegisterGuestPanel;
import com.frame.small.RegisterStaffPanel;
import com.frame.small.ReportsPanel;
import com.frame.small.StatisticsPanel;
import com.service.ContainerService;

import javax.swing.JSplitPane;

public class AdminMainPanel extends JTabbedPane {

	public AdminMainPanel() {
		super();
		setSize(ContainerService.panelWidth, ContainerService.panelHeight);
		Panel homePanel = new HomePanel();
		Panel registerStaffPanel = new RegisterStaffPanel();
		Panel registerGuestPanel = new RegisterGuestPanel();
		Panel manageStaffPanel = new ManageStaffPanel();
		Panel managePricesPanel = new ManagePricesPanel();
		Panel manageServicePanel =  new ManageServicesPanel();
		Panel reportsPanel = new ReportsPanel();
		Panel statisticsPanel = new StatisticsPanel();
		
		addTab("Home", (Component) homePanel);
		addTab("Register Staff",(Component)  registerStaffPanel);
		addTab("Register Guest",(Component) registerGuestPanel);
		addTab("Manage Staff",(Component)  manageStaffPanel);
		addTab("Manage Service/Rooms",(Component)  manageServicePanel);
		addTab("Manage Pricing",(Component)  managePricesPanel);
		addTab("Reports",(Component)  reportsPanel);
		addTab("Statistics",(Component)  statisticsPanel);
		addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	   Component selectedComponent = getSelectedComponent();
                   if (selectedComponent instanceof Panel) {
                       ((Panel) selectedComponent).reset();
                   }
            }
        });

	}
	
}

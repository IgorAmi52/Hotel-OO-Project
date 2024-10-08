package com.frame.small;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePickerImpl;

import com.exceptions.NoPricingException;
import com.frame.Panel;
import com.models.Guest;
import com.models.Reservation;
import com.models.enums.ReservationStatus;
import com.service.ContainerService;
import com.service.DateLabelFormatter;
import com.service.Holder;
import com.service.PricingServiceInterface;
import com.service.ReservationService;
import com.service.RoomServiceInterface;

public class MakeReservationPanel extends JPanel implements Panel {

	private PricingServiceInterface pricingService = Holder.getInstance().getPricingService();
	private RoomServiceInterface roomService = Holder.getInstance().getRoomService();
	private ReservationService reservationService = Holder.getInstance().getReservationService();

	private static final long serialVersionUID = 1L;
	private JComboBox<String> roomTypeBox;
	private String[] roomTypes = { "temp" };
	private ArrayList<JCheckBox> addCheckBoxes = new ArrayList<JCheckBox>();
	private JLabel successLabel;
	private JLabel errorLabel;
	private String[][] resData;
	private Reservation[] reservations;
	private String[] columnNames = { "Room Type", "Check-in", "Check-out", "Additionals", "Status", "Price",
			"Comment" };
	private JTable table;
	private String[] addServiceArr;
	private JLabel totalSpentLabel;

	public MakeReservationPanel() {
		super();
		setLayout(null);
		setSize(ContainerService.panelWidth, ContainerService.panelHeight);

		JLabel lblNewLabel = new JLabel("Room Type:");
		lblNewLabel.setBounds(484, 122, 92, 16);
		add(lblNewLabel);

		roomTypeBox = new JComboBox<String>(roomTypes);
		roomTypeBox.setBounds(650, 118, 228, 27);
		add(roomTypeBox);

		JLabel lblNewLabel_5 = new JLabel("Additionals:");
		lblNewLabel_5.setBounds(484, 182, 92, 16);
		add(lblNewLabel_5);

		addServiceArr = roomService.getAddServicesArr();

		for (int i = 0; i < addServiceArr.length; i++) {
			addCheckBoxes.add(new JCheckBox(addServiceArr[i]));
		}
		JPanel checkBoxPanel = new JPanel(new GridLayout(0, 1));
		for (int i = 0; i < addCheckBoxes.size(); i++) {
			checkBoxPanel.add(addCheckBoxes.get(i));
		}
		checkBoxPanel.setLocation(650, 182);
		JScrollPane addScrollPane = new JScrollPane(checkBoxPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		addScrollPane.setSize(228, 80);
		addScrollPane.setLocation(650, 182);

		add(addScrollPane);

		JDatePickerImpl checkinDatePicker = ContainerService.getDatePicker();
		JDatePickerImpl checkoutDatePicker = ContainerService.getDatePicker();

		checkinDatePicker.setBounds(191, 122, 228, 29);
		checkoutDatePicker.setBounds(191, 182, 228, 29);
		add(checkinDatePicker);
		add(checkoutDatePicker);

		JLabel lblNewLabel_1 = new JLabel("Check-in Date:");
		lblNewLabel_1.setBounds(73, 122, 103, 16);
		add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Check-out Date:");
		lblNewLabel_2.setBounds(73, 182, 118, 16);
		add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("Request new Reservation:");
		lblNewLabel_3.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_3.setBounds(72, 58, 347, 29);
		add(lblNewLabel_3);

		successLabel = new JLabel("");
		successLabel.setHorizontalAlignment(SwingConstants.CENTER);
		successLabel.setForeground(new Color(0, 183, 28));
		successLabel.setFont(new Font("KufiStandardGK", Font.PLAIN, 17));
		successLabel.setBounds(268, 20, 476, 25);
		add(successLabel);

		JButton requestReservationButton = new JButton("Request a Reservation");
		requestReservationButton.setFont(new Font("Lucida Grande", Font.PLAIN, 15));

		requestReservationButton.setBounds(73, 258, 370, 43);
		add(requestReservationButton);

		table = new JTable(new DefaultTableModel(resData, columnNames));
		table.setForeground(new Color(0, 0, 0));
		JScrollPane scrollPane = new JScrollPane(table);

		scrollPane.setBounds(73, 373, 807, 200);
		add(scrollPane);

		JLabel lblNewLabel_4 = new JLabel("Total money spent:");
		lblNewLabel_4.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		lblNewLabel_4.setBounds(73, 331, 162, 30);
		add(lblNewLabel_4);

		JButton cancelReservationButton = new JButton("Cancel Reservation");

		cancelReservationButton.setEnabled(false);
		cancelReservationButton.setBounds(668, 332, 210, 29);
		add(cancelReservationButton);

		errorLabel = new JLabel("");
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorLabel.setForeground(new Color(255, 28, 14));
		errorLabel.setFont(new Font("KufiStandardGK", Font.PLAIN, 17));
		errorLabel.setBounds(268, 21, 476, 25);
		add(errorLabel);

		totalSpentLabel = new JLabel("0 RSD");
		totalSpentLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		totalSpentLabel.setBounds(247, 331, 172, 30);
		add(totalSpentLabel);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) { // To prevent multiple events when selection is still being adjusted
					int selectedRow = table.getSelectedRow();
					if (selectedRow != -1) { // If a row is selected
						String status = (String) table.getValueAt(selectedRow, 4);
						if (status.equals(ReservationStatus.PENDING.getStatus())) {
							cancelReservationButton.setEnabled(true);
						}
					} else {
						cancelReservationButton.setEnabled(false);
					}
				}
			}
		});
		cancelReservationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				try {

					Reservation cancelReservation = reservations[selectedRow];
					reservationService.cancelReservation(cancelReservation);

					reservations = reservationService.getReservations(Holder.getInstance().getUser());
					resData = setData(reservations);

					table.setModel(new DefaultTableModel(resData, columnNames));
					successLabel.setText("Reservation cancelled successfully!");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		requestReservationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String checkInDate = checkinDatePicker.getJFormattedTextField().getText();
				String checkOutDate = checkoutDatePicker.getJFormattedTextField().getText();
				String roomType = roomTypeBox.getSelectedItem().toString();
				String[] addServiceArr = ContainerService.getSelectedValues(addCheckBoxes);
				Guest guest = (Guest) Holder.getInstance().getUser();

				if (checkInDate.isEmpty() || checkOutDate.isEmpty()) {
					successLabel.setText("");
					errorLabel.setText("Dates are required fields!");
					return;
				}
				String todayDate = DateLabelFormatter.getTodaysDateStr();
				if (DateLabelFormatter.isFirstDateGreater(todayDate, checkInDate)) {
					successLabel.setText("");
					errorLabel.setText("You can't reserve in the past!");
					return;
				}
				if (checkInDate.equals(checkOutDate)) {
					successLabel.setText("");
					errorLabel.setText("Check-in and check-out must be different!");
					return;
				}
				try {
					Reservation reservation = new Reservation(checkInDate, checkOutDate, roomType, addServiceArr,
							guest);

					reservation.setPricing(pricingService.calculatePricing(reservation));

					reservationService.requestReservation(reservation);
					ContainerService.resetFields(MakeReservationPanel.this);

					reservations = reservationService.getReservations(Holder.getInstance().getUser());
					resData = setData(reservations);
					table.setModel(new DefaultTableModel(resData, columnNames));

					successLabel.setText("");
					successLabel.setText("Reservation was successfully submited!");
					errorLabel.setText("");

				} catch (NoPricingException e1) {
					successLabel.setText("");
					errorLabel.setText(e1.getMessage());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

	}

	private String[][] setData(Reservation[] reservations) {
		String[][] ret = new String[reservations.length][];
		int i = 0;
		for (Reservation reservation : reservations) {
			String roomType = reservation.getRoomType();
			String checkInDate = reservation.getCheckInDate();
			String checkOutDate = reservation.getCheckOutDate();
			String additionals = String.join(", ", reservation.getAddServices());
			String status = reservation.getStatus();
			String price = Double.toString(reservation.getPrice());
			String comment = reservation.getComment();

			String[] row = { roomType, checkInDate, checkOutDate, additionals, status, price, comment };
			ret[i++] = row;
		}
		return ret;
	}

	private String getTotalSpentString(Reservation[] reservations) {
		double total = 0;
		for (Reservation reservation : reservations) {
			if (reservation.getStatus() != ReservationStatus.REJECTED.getStatus()
					&& reservation.getStatus() != ReservationStatus.PENDING.getStatus()) {
				total += reservation.getPrice();
			}
		}
		return Double.toString(total);
	}

	@Override
	public void reset() {
		ContainerService.resetFields(this);
		try {
			roomTypes = roomService.getRoomTypes();
			roomTypeBox.removeAllItems();
			for (String type : roomTypes) {
				roomTypeBox.addItem(type);
			}
			reservations = reservationService.getReservations(Holder.getInstance().getUser());
			resData = setData(reservations);
			totalSpentLabel.setText(getTotalSpentString(reservations) + " RSD");
			addServiceArr = roomService.getAddServicesArr();
			table.setModel(new DefaultTableModel(resData, columnNames));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		successLabel.setText("");
		errorLabel.setText("");
	}
}

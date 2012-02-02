/*
 * Created on Mar 9, 2004
 */
package rustleund.fightingfantasy.framework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.html.HTMLDocument;

/**
 * @author rustlea
 */
public class GameView extends JPanel implements ActionListener {

	private static final long serialVersionUID = -8223557610157876489L;

	private GameController controller = null;
	private JLabel messageLabel = null;

	private JEditorPane htmlEditorPane;
	private JScrollPane htmlEditorScrollPane;

	private JList inventoryList = null;
	private JLabel skill = null;
	private JLabel stamina = null;
	private JLabel luck = null;
	private JLabel provisions = null;
	private JLabel honor = null;
	private JLabel nemesis = null;
	private JLabel gold = null;
	private JLabel time = null;

	public GameView(GameController controller) {

		this.controller = controller;

		setLayout(new BorderLayout());

		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		messageLabel = new JLabel("");
		messageLabel.setForeground(Color.RED);

		JPanel messagePanel = new JPanel();
		messagePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		messagePanel.add(messageLabel);

		add(messagePanel, BorderLayout.NORTH);

		htmlEditorPane = new JEditorPane("text/html", "");
		htmlEditorPane.setEditable(false);
		((HTMLDocument) htmlEditorPane.getDocument()).setBase(getBaseUrl());
		htmlEditorPane.addHyperlinkListener(controller);

		this.htmlEditorScrollPane = new JScrollPane(htmlEditorPane);
		this.htmlEditorScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.htmlEditorScrollPane.setPreferredSize(new Dimension(640, 480));
		this.htmlEditorScrollPane.setMinimumSize(new Dimension(10, 10));

		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Description"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		descriptionPanel.add(this.htmlEditorScrollPane);

		add(descriptionPanel, BorderLayout.CENTER);

		DefaultComboBoxModel inventoryComboBox = new DefaultComboBoxModel();
		inventoryList = new JList(inventoryComboBox);
		JScrollPane inventoryScrollPane = new JScrollPane(inventoryList);
		inventoryScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		inventoryScrollPane.setPreferredSize(new Dimension(200, 300));
		inventoryScrollPane.setMinimumSize(new Dimension(200, 300));

		JPanel inventoryPanel = new JPanel();
		inventoryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Inventory"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		inventoryPanel.setLayout(new BorderLayout());

		inventoryPanel.add(inventoryScrollPane, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		buttonsPanel.setLayout(new GridLayout(2, 0));

		JButton inventoryButton = new JButton("Use Item");
		inventoryButton.setActionCommand("use item");
		inventoryButton.addActionListener(this);
		buttonsPanel.add(inventoryButton);

		JButton eatMealButton = new JButton("Eat Meal");
		eatMealButton.setActionCommand("eat meal");
		eatMealButton.addActionListener(this);
		buttonsPanel.add(eatMealButton);

		inventoryPanel.add(buttonsPanel, BorderLayout.SOUTH);

		add(inventoryPanel, BorderLayout.EAST);

		JPanel statsPanel = new JPanel(new GridLayout(3, 6));
		statsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Player Stats"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		statsPanel.add(new JLabel("Skill"));
		skill = new JLabel("");
		statsPanel.add(skill);

		statsPanel.add(new JLabel("Stamina"));
		stamina = new JLabel("");
		statsPanel.add(stamina);

		statsPanel.add(new JLabel("Luck"));
		luck = new JLabel("");
		statsPanel.add(luck);

		statsPanel.add(new JLabel("Provisions"));
		provisions = new JLabel("");
		statsPanel.add(provisions);

		statsPanel.add(new JLabel("Honor"));
		honor = new JLabel("");
		statsPanel.add(honor);

		statsPanel.add(new JLabel("Nemesis"));
		nemesis = new JLabel("");
		statsPanel.add(nemesis);

		statsPanel.add(new JLabel("Gold"));
		gold = new JLabel("");
		statsPanel.add(gold);

		statsPanel.add(new JLabel("Time"));
		time = new JLabel("");
		statsPanel.add(time);

		add(statsPanel, BorderLayout.SOUTH);

	}

	private URL getBaseUrl() {
		URL result = null;
		try {
			result = new URL("file:///" + System.getProperty("user.dir") + "/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if ("use item".equals(event.getActionCommand())) {
			Item selectedItem = (Item) inventoryList.getSelectedValue();
			if (selectedItem != null) {
				controller.useItem(selectedItem);
			}
		} else if ("eat meal".equals(event.getActionCommand())) {
			controller.eatMeal();
		}
	}

	public void update(GameState gameState) {
		this.messageLabel.setText(gameState.getMessage());

		this.htmlEditorPane.setText(gameState.getPageState().getPagetext());
		if (!gameState.isPageLoaded()) {
			this.htmlEditorPane.setCaretPosition(0);
			gameState.setPageLoaded(true);
		}

		PlayerState playerState = gameState.getPlayerState();

		inventoryList.setModel(new DefaultComboBoxModel(playerState.getItems().values().toArray()));

		skill.setText(playerState.getSkill().toString());
		stamina.setText(playerState.getStamina().toString());
		luck.setText(playerState.getLuck().toString());
		provisions.setText(playerState.getProvisions().toString());
		honor.setText(playerState.getHonor().toString());
		nemesis.setText(playerState.getNemesis().toString());
		gold.setText(playerState.getGold().toString());
		time.setText(playerState.getTime().toString());

		repaint();
	}

}
/*
 * Created on Mar 9, 2004
 */
package rustleund.fightingfantasy.framework.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
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
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

/**
 * @author rustlea
 */
public class SwingGameView extends JPanel implements ActionListener, GameView, HyperlinkListener {

	@Serial
	private static final long serialVersionUID = -8223557610157876489L;

	private final transient GameController controller;
	private final JLabel messageLabel;

	private final ImageView imageLabel;

	private final JEditorPane htmlEditorPane;

	private final JList<Item> inventoryList;
	private final JLabel skill;
	private final JLabel stamina;
	private final JLabel luck;
	private final JLabel provisions;
	private final JLabel honor;
	private final JLabel nemesis;
	private final JLabel gold;
	private final JLabel time;

	public SwingGameView(GameController controller) {

		this.controller = controller;

		setLayout(new BorderLayout());

		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		messageLabel = new JLabel("");
		messageLabel.setForeground(Color.RED);

		JPanel messagePanel = new JPanel();
		messagePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		messagePanel.add(messageLabel);

		add(messagePanel, BorderLayout.NORTH);

		imageLabel = new ImageView(280, 480); // image 560 x 960 for retina
		imageLabel.setPreferredSize(new Dimension(280, 480));

		JPanel imagePanel = new JPanel();
		imagePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Image"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		imagePanel.add(imageLabel);

		add(imagePanel, BorderLayout.WEST);

		htmlEditorPane = new JEditorPane("text/html", "");
		htmlEditorPane.setEditable(false);
		HTMLDocument htmlDocument = (HTMLDocument) htmlEditorPane.getDocument();
		htmlDocument.setBase(getBaseUrl());
		StyleSheet styleSheet = htmlDocument.getStyleSheet();
		styleSheet.addRule("body { font-family: \"Arial\",arial,sans-serif; margin: 5px; }");
		htmlEditorPane.addHyperlinkListener(this);

		JScrollPane htmlEditorScrollPane = new JScrollPane(htmlEditorPane);
		htmlEditorScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		htmlEditorScrollPane.setPreferredSize(new Dimension(640, 480));
		htmlEditorScrollPane.setMinimumSize(new Dimension(10, 10));

		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Description"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		descriptionPanel.add(htmlEditorScrollPane);

		add(descriptionPanel, BorderLayout.CENTER);

		DefaultComboBoxModel<Item> inventoryComboBox = new DefaultComboBoxModel<>();
		inventoryList = new JList<>(inventoryComboBox);
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
			Item selectedItem = inventoryList.getSelectedValue();
			if (selectedItem != null) {
				controller.useItem(selectedItem);
			}
		} else if ("eat meal".equals(event.getActionCommand())) {
			controller.eatMeal();
		}
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String command = e.getURL().getHost();
			int port = e.getURL().getPort();
			switch (command) {
				case "link" -> controller.goToPage("" + port);
				case "buyItem" -> controller.addItemToInventory(port);
				case "domulti" -> controller.doMultiCommand(port);
				case "dobattle" -> controller.doBattle(port);
				case "doflee" -> controller.doFlee(port);
				case "testluckbattle" -> controller.doTestLuckInBattle(port == 0);
				default -> throw new IllegalArgumentException("Unknown link type");
			}
		}
	}

	@Override
	public void update(GameState gameState) {
		this.messageLabel.setText(gameState.getMessage());

		this.htmlEditorPane.setText(gameState.getPageState().getPagetext());
		if (!gameState.isPageLoaded()) {
			this.htmlEditorPane.setCaretPosition(0);
			gameState.setPageLoaded(true);
		}

		imageLabel.update(gameState);

		PlayerState playerState = gameState.getPlayerState();

		inventoryList.setModel(new DefaultComboBoxModel<>(playerState.getItems().values().toArray(new Item[0])));

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
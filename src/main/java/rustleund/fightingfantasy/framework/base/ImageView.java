package rustleund.fightingfantasy.framework.base;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import com.google.common.base.Throwables;

public class ImageView extends JComponent {

	private static final long serialVersionUID = 6915472531410686968L;

	private BufferedImage image;
	private int width;
	private int height;

	public ImageView(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (image != null) {
			Rectangle r = determineDrawRectangle();
			Image drawableImage = image.getScaledInstance(r.width * 2, r.height * 2, BufferedImage.SCALE_AREA_AVERAGING);
			g.drawImage(drawableImage, r.x, r.y, r.width, r.height, null);
		}
	}

	private Rectangle determineDrawRectangle() {
		double scale = Math.min(1., Math.min((double) width / image.getWidth(), (double) height / image.getHeight()));
		int w = (int) Math.round(image.getWidth() * scale);
		int h = (int) Math.round(image.getHeight() * scale);
		int x = (width - w) / 2;
		int y = (height - h) / 2;
		return new Rectangle(x, y, w, h);
	}

	public void update(GameState gameState) {
		File imageLocation = new File(gameState.getImagesDirectory(), gameState.getPageState().getPageName() + ".png");
		if (imageLocation.exists()) {
			try {
				image = ImageIO.read(imageLocation);
			} catch (IOException e) {
				Throwables.propagate(e);
			}
		} else {
			image = null;
		}
	}
}

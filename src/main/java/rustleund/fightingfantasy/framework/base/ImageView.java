package rustleund.fightingfantasy.framework.base;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import static java.awt.Image.SCALE_AREA_AVERAGING;

public class ImageView extends JComponent {

	@Serial
	private static final long serialVersionUID = 6915472531410686968L;

	private transient BufferedImage image;
	private final int imageWidth;
	private final int imageHeight;

	public ImageView(int imageWidth, int imageHeight) {
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (image != null) {
			Rectangle r = determineDrawRectangle();
			Image drawableImage = image.getScaledInstance(r.width * 2, r.height * 2, SCALE_AREA_AVERAGING);
			g.drawImage(drawableImage, r.x, r.y, r.width, r.height, null);
		}
	}

	private Rectangle determineDrawRectangle() {
		double scale = Math.min(1., Math.min((double) imageWidth / image.getWidth(), (double) imageHeight / image.getHeight()));
		int w = (int) Math.round(image.getWidth() * scale);
		int h = (int) Math.round(image.getHeight() * scale);
		int x = (imageWidth - w) / 2;
		int y = (imageHeight - h) / 2;
		return new Rectangle(x, y, w, h);
	}

	public void update(GameState gameState) {
		Path imageLocation = gameState.getImagesDirectory().resolve(gameState.getPageState().getPageName() + ".png");
		if (Files.exists(imageLocation)) {
			try (InputStream is = Files.newInputStream(imageLocation)) {
				image = ImageIO.read(is);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			image = null;
		}
	}
}

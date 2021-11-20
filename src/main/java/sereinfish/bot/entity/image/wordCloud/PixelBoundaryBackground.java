package sereinfish.bot.entity.image.wordCloud;

import com.kennycason.kumo.bg.Background;
import com.kennycason.kumo.collide.RectanglePixelCollidable;
import com.kennycason.kumo.image.CollisionRaster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PixelBoundaryBackground implements Background {
    private final CollisionRaster collisionRaster;
    private final Point position;

    public PixelBoundaryBackground(InputStream imageInputStream) throws IOException {
        this.position = new Point(0, 0);
        BufferedImage bufferedImage = ImageIO.read(imageInputStream);
        this.collisionRaster = new CollisionRaster(bufferedImage);
    }

    public PixelBoundaryBackground(BufferedImage bufferedImage){
        this.position = new Point(0, 0);
        this.collisionRaster = new CollisionRaster(bufferedImage);
    }

    public PixelBoundaryBackground(File file) throws IOException {
        this((InputStream)(new FileInputStream(file)));
    }

    public PixelBoundaryBackground(String filepath) throws IOException {
        this(new File(filepath));
    }

    public void mask(RectanglePixelCollidable background) {
        Dimension dimensionOfShape = this.collisionRaster.getDimension();
        Dimension dimensionOfBackground = background.getDimension();
        int minY = Math.max(this.position.y, 0);
        int minX = Math.max(this.position.x, 0);
        int maxY = dimensionOfShape.height - this.position.y - 1;
        int maxX = dimensionOfShape.width - this.position.x - 1;
        CollisionRaster rasterOfBackground = background.getCollisionRaster();

        for(int y = 0; y < dimensionOfBackground.height; ++y) {
            int x;
            if (y >= minY && y <= maxY) {
                for(x = 0; x < dimensionOfBackground.width; ++x) {
                    if (x < minX || x > maxX || this.collisionRaster.isTransparent(x, y)) {
                        rasterOfBackground.setPixelIsNotTransparent(this.position.x + x, this.position.y + y);
                    }
                }
            } else {
                for(x = 0; x < dimensionOfBackground.width; ++x) {
                    rasterOfBackground.setPixelIsNotTransparent(this.position.x + x, this.position.y + y);
                }
            }
        }

    }
}

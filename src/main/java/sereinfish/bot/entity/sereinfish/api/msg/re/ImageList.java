package sereinfish.bot.entity.sereinfish.api.msg.re;

import lombok.Getter;
import sereinfish.bot.entity.sereinfish.api.msg.ImageItem;

import java.util.ArrayList;

@Getter
public class ImageList extends Msg {
    ArrayList<ImageItem> imageItems = new ArrayList<>();
}

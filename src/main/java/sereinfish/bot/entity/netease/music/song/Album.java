package sereinfish.bot.entity.netease.music.song;

import lombok.Data;

@Data
public class Album {
    long publishTime;
    int size;
    Artist artist;
    int copyrightId;
    String name;
    long id;
    long picId;
    String[] alia;
    int mark;
    int status;
}

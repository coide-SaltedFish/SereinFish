package sereinfish.bot.entity.image.wordCloud;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.ColorPalette;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import sereinfish.bot.entity.arknights.penguinStatistics.PenguinStatistics;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.net.mc.ServerListPing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MyWordCloud {

    public static BufferedImage getWordCloud(List<String> strList, BufferedImage background){
        List<String> words = new ArrayList<>();
        for (String str:strList){
            List<Word> w = WordSegmenter.segWithStopWords(str);
            for (Word word:w){
                words.add(word.getText());
            }
        }

        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(5000);
        frequencyAnalyzer.setMinWordLength(2);

        // 引入中文解析器
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

        final List<WordFrequency> wordFrequencyList = frequencyAnalyzer.load(words);

        // 设置图片分辨率
        Dimension dimension = new Dimension(background.getWidth(), background.getHeight());
        // 此处的设置采用内置常量即可，生成词云对象
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        Font font;//字体
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, PenguinStatistics.class.getClassLoader().getResourceAsStream("arknights/fonts/萝莉体.ttf"));
        } catch (Exception e) {
            SfLog.getInstance().e(ServerListPing.class, "默认字体加载失败：" + ServerListPing.class.getClassLoader().getResource(FileHandle.mcResDefaultFontFile), e);
            font = new Font("微软雅黑 Light", Font.PLAIN, 24);
        }
        wordCloud.setKumoFont(new KumoFont(font));
        wordCloud.setPadding(2);
        //wordCloud.setColorPalette(new ColorPalette(new Color(0xed1941), new Color(0xf26522), new Color(0x845538),new Color(0x8a5d19),new Color(0x7f7522),new Color(0x5c7a29),new Color(0x1d953f),new Color(0x007d65),new Color(0x65c294)));

        wordCloud.setBackground(new PixelBoundaryBackground(background));
        wordCloud.setBackgroundColor(new Color(255, 255, 255));
        // 生成词云
        wordCloud.build(wordFrequencyList);

        return wordCloud.getBufferedImage();
    }
}

package sereinfish.bot.entity.ffmpeg;

import ws.schild.jave.*;

import java.io.File;

public class AudioHandle {

    /**
     * amr转mp3
     * @param source
     * @param target
     * @throws EncoderException
     */
    public static void amrToMp3 (File source, File target) throws EncoderException {
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();
        MultimediaObject multimediaObject  = new MultimediaObject(source);
        encoder.encode(multimediaObject,target, attrs);
    }

    /**
     * Mp3转Amr
     * @param source
     * @param target
     * @throws EncoderException
     */
    public static void mp3ToAmr (File source, File target) throws EncoderException {
        MultimediaObject multimediaObject  = new MultimediaObject(source);

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libopencore_amrnb");

        audio.setBitRate(12200);//比特率
        audio.setChannels(1);//声道；1单声道，2立体声
        audio.setSamplingRate(8000);//采样率（重要！！！）

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("amr");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();

        encoder.encode(multimediaObject,target, attrs);
    }
}

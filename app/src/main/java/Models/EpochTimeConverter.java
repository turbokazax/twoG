package Models;

public class EpochTimeConverter {
    private long epoch;
    private long date;
    public EpochTimeConverter(){
        this.epoch=	System.currentTimeMillis()/1000;
    }
    public long getEpochTime(){
        return System.currentTimeMillis()/1000;
    }
}

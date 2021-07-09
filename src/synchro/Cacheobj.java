package synchro;

import java.io.Serializable;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class Cacheobj implements Serializable {
	
	public String path;
	public long size;
	public boolean isdir;
	public transient FileLoadBar fileLoadBar;

	public Cacheobj(String path, Long size, boolean isdir) {
		
		this.path = path;
		this.size = size;
		this.isdir = isdir;		
		this.fileLoadBar = new FileLoadBar();
		
		this.fileLoadBar.controller.file_size.setText(humanReadableByteCountBin(size));
		
	}
	
	public static String humanReadableByteCountBin(long bytes) {
	    long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
	    if (absB < 1024) {
	        return bytes + " B";
	    }
	    long value = absB;
	    CharacterIterator ci = new StringCharacterIterator("KMGTPE");
	    for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
	        value >>= 10;
	        ci.next();
	    }
	    value *= Long.signum(bytes);
	    return String.format("%.1f %cB", value / 1024.0, ci.current());
	}

}

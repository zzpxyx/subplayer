package subplayer;

public class Subtitle implements Comparable<Subtitle> {
	public long startTime;
	public long endTime;
	public String text;

	@Override
	public int compareTo(Subtitle o) {
		if (this.startTime < o.startTime) {
			return -1;
		} else if (this.startTime > o.startTime) {
			return 1;
		}
		return 0;
	}
}


public class Point {

	int id;
	int x;
	int y;
	
	public Point() {
		id=0;
		x=0;
		y=0;
	}
	
	public Point(int id, int x, int y) {
		super();
		this.id=id;
		this.x = x;
		this.y = y;
	}
	
	public static double distance(Point a,Point b) {
		return Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
	}
}

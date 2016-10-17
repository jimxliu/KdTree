import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import java.util.ArrayList;


public class KdTree {

	private Node root;
	private int size;
   	public KdTree() { // construct an empty set of points		
	}	 
   	public boolean isEmpty(){  // is the set empty? 
		return size == 0;
	}  	
	public int size(){  // number of points in the set 
		return size;
	}   
	public void insert(Point2D p){// add the point to the set (if it is not already in the set)
		if(!contains(p)){				
			root = insert(root, p, true, new RectHV(0.0, 0.0, 1.0, 1.0));
			size++;	
		}	
	}
	private Node insert(Node node, Point2D p, boolean isVertical, RectHV rect){
		if(node == null){ 
			node = new Node(p, rect);
			return node;
		}
		if(isVertical){
			if(p.x() < node.point.x()){
				RectHV r = new RectHV(rect.xmin(), rect.ymin(), node.point.x(), rect.ymax());
				node.lb = insert(node.lb, p, !isVertical, r);
			} else {
				RectHV r = new RectHV(node.point.x(), rect.ymin(), rect.xmax(), rect.ymax());
				node.rt = insert(node.rt, p, !isVertical, r);
			}
		} else {
			if(p.y() < node.point.y()){
				RectHV r = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.point.y());
				node.lb = insert(node.lb, p, !isVertical, r);
			} else {
				RectHV r = new RectHV(rect.xmin(), node.point.y(), rect.xmax(), rect.ymax());
				node.rt = insert(node.rt, p, !isVertical, r);
			}
		}
		return node;
	}
   	public boolean contains(Point2D p){// does the set contain point p? 
		Node node = root;
		boolean isVertical = true;
		while(node != null){
			if(p.equals(node.point))
				return true;			
			if(isVertical){
				if(p.x() < node.point.x())
					node = node.lb;
				else 
					node = node.rt;
			} else {
				if(p.y() < node.point.y())
					node = node.lb;
				else 
					node = node.rt;
			}
			isVertical = !isVertical;
		}
		return false;
	}

	public void draw(){ // draw all points to standard draw 
		Node cursor = root;
		draw(cursor, true);		
	}
	private void draw(Node node, boolean isVertical){
		if(node != null){
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.setPenRadius(0.01);
			StdDraw.point(node.point.x(), node.point.y());
			if(isVertical){
				StdDraw.setPenColor(StdDraw.RED);
				StdDraw.setPenRadius(0.002);
				StdDraw.line(node.point.x(), node.rect.ymin(), node.point.x(), node.rect.ymax());
			} else{ 
				StdDraw.setPenColor(StdDraw.BLUE);
				StdDraw.setPenRadius(0.002);
				StdDraw.line(node.rect.xmin(), node.point.y(), node.rect.xmax(), node.point.y());
			}			
			draw(node.lb, !isVertical);
			draw(node.rt, !isVertical);
		}
	}
	
	public Iterable<Point2D> range(RectHV rect){ // all points that are inside the rectangle 
		ArrayList<Point2D> list = new ArrayList<>();
		range(rect, root, list);
		return list;
	}   
	private void range(RectHV query,  Node node, ArrayList<Point2D> list){
		if(node != null){
			if(query.contains(node.point))
				list.add(node.point);
			if(node.lb != null && query.intersects(node.lb.rect))
				range(query, node.lb, list);
			if(node.rt != null && query.intersects(node.rt.rect))
				range(query, node.rt, list);
		}
	}
		
	public Point2D nearest(Point2D p){  // a nearest neighbor in the set to point p; null if the set is empty 
		if(isEmpty())
			return null;
		return nearest(p, root, root.point);
	}
	private Point2D nearest(Point2D query, Node node, Point2D closest){
		if(query.distanceSquaredTo(node.point) < query.distanceSquaredTo(closest))
			closest = node.point;
		if(node.lb != null && (node.lb.rect.distanceSquaredTo(query) < closest.distanceSquaredTo(query)))
			closest = nearest(query, node.lb, closest);
		if(node.rt != null && (node.rt.rect.distanceSquaredTo(query) < closest.distanceSquaredTo(query)))
			closest = nearest(query, node.rt, closest);
		return closest;
	}
	private static class Node{
		public Point2D point;
		public RectHV rect;
		public Node lb;
		public Node rt;

		public Node(Point2D point, RectHV rect){
			this.point = point;
			this.rect = rect;
		}
	}
   	public static void main(String[] args){ // unit testing of the methods (optional) 
		KdTree kt = new KdTree();
		kt.insert(new Point2D(0.1,0.1));
		kt.insert(new Point2D(0.2,0.2));
		kt.insert(new Point2D(0.3,0.3));
		kt.insert(new Point2D(0.3,0.4));
		kt.insert(new Point2D(0.5,0.3));
		kt.draw();
		Point2D p = new Point2D(0.2, 0.45);
		StdDraw.setPenColor(StdDraw.MAGENTA);
		StdDraw.setPenRadius(0.01);
		p.draw();
		System.out.println("The point closest to (0.2, 0.45) is " + kt.nearest(p));
	}
}

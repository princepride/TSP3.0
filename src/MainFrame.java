import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

public class MainFrame extends JFrame{

	public static final int vector=25;
	public static final int sampleNum=50;
	public static final int range=800;
	public static final double pm=0.01;
	public static final double pc=0.8;
	public static final int times=100000;
	static double[][] timeArray=new double[sampleNum+1][sampleNum];
	HashMap<Integer, Point> maps=new HashMap();
	static double[][] matrix=new double[vector][vector];
	static int[][][] array=new int[sampleNum+1][sampleNum][vector];
	static int[][] insence=new int[sampleNum+1][vector];
	static int[][][] temparray=new int[sampleNum+1][sampleNum][vector];
	static MainFrame mainFrame;
	static int[][] LCSMatrix=new int[vector+1][vector+1];
	static int[] tempArray=new int[vector];
	static int[] costArray=new int[times/10000];
	@Override
	public void paint(Graphics arg0) {
		super.paint(arg0);
		for(int i=0;i<vector-1;i++) {
			arg0.fillOval(maps.get(array[sampleNum][0][i]).x-5+100, maps.get(array[sampleNum][0][i]).y-5+50, 10, 10);
			arg0.drawLine(maps.get(array[sampleNum][0][i]).x+100, maps.get(array[sampleNum][0][i]).y+50, maps.get(array[sampleNum][0][i+1]).x+100, maps.get(array[sampleNum][0][i+1]).y+50);
		}
		arg0.fillOval(maps.get(array[sampleNum][0][vector-1]).x-5+100, maps.get(array[sampleNum][0][vector-1]).y-5+50, 10, 10);
		arg0.drawLine(maps.get(array[sampleNum][0][vector-1]).x+100, maps.get(array[sampleNum][0][vector-1]).y+50, maps.get(array[sampleNum][0][0]).x+100, maps.get(array[sampleNum][0][0]).y+50);
		arg0.drawRect(range+300, 200, 400, 300);
		int deta=costArray[times/10000-1]-costArray[0];
		if(costArray[0]==costArray[times/10000-1]) {
			arg0.drawLine(range+300, 480, range+300+(times/10000-1)*40, 480);
		}
		else {
			for(int i=0;i<times/10000-1;i++) {
				arg0.drawLine(range+300+i*40, 480+(costArray[i]-costArray[times/10000-1])*260/deta, range+300+(i+1)*40, 480+(costArray[i+1]-costArray[times/10000-1])*260/deta);
			}
		}
		arg0.drawString("���·��ֵΪ��"+Integer.toString(costArray[times/10000-1]), range+300, 600);
		
	}
	static class TestThread implements Runnable{

		int a;
		public TestThread(int a) {
			this.a=a;
		}
		public void run() {
			mainFrame.createArray(a);

			for(int i=0;i<times;i++) {
				mainFrame.createTimeArray(a);
				mainFrame.reproduce(a);
/*				if(i%10000==0) {
					System.out.println(timeArray[0]);
				}*/
			}
			mainFrame.quickSort(a,0, sampleNum);
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mainFrame=new MainFrame();
		mainFrame.createMap();
		mainFrame.createMatrix();
		ExecutorService pool=Executors.newFixedThreadPool(8);
		for(int i=0;i<sampleNum;i++) {
			TestThread testThread=new TestThread(i);
			pool.submit(testThread);
		}
		pool.shutdown();
		try {
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			System.out.println("finish");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<sampleNum;i++) {
			for(int j=0;j<vector;j++) {
				array[sampleNum][i][j]=array[i][0][j];
			}
		}
		mainFrame.createTimeArray(sampleNum);
		int goal=LCS(array[sampleNum][0], array[sampleNum][1]);
		for(int i=0;i<sampleNum;i++) {
			recombination(goal, array[sampleNum][i]);
		}
		for(int i=0;i<times;i++) {
			mainFrame.reproduce(sampleNum);
			mainFrame.createTimeArray(sampleNum);
				if(i%10000==0) {
				System.out.println(timeArray[sampleNum][0]);
				costArray[i/10000]=(int)timeArray[sampleNum][0];
			}
		}

/*		mainFrame.createArray();

		for(int i=0;i<times;i++) {
			mainFrame.createTimeArray();
			mainFrame.reproduce();
			if(i%10000==0) {
				System.out.println(timeArray[0]);
				points[i/10000]=(int)timeArray[0];
			}
		}
		mainFrame.quickSort(0, sampleNum);*/
		
		
/*		for(int i=0;i<vector;i++) {
			System.out.println(mainFrame.maps.get(array[0][i]).x+" "+mainFrame.maps.get(array[0][i]).y);
		}*/
//		mainFrame.createMatrix();
		mainFrame.launch();
	}

	void launch() {
		setBounds(0, 0, range+800, range+200);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				super.windowClosing(arg0);
				System.exit(0);
			}
		});
	}
	void createMap() {
/*		Random random=new Random();
		for(int i=0;i<vector;i++) {
			Point point=new Point(i,Math.abs(random.nextInt())%range,Math.abs(random.nextInt())%range);
			maps.put(i, point);
		}*/
		try {
			String filePath = new File("").getAbsolutePath();
			FileReader fileReader=new FileReader(filePath+"\\Points.txt");
			BufferedReader bufferedReader=new BufferedReader(fileReader);
			for(int i=0;i<vector;i++) {
				String string=bufferedReader.readLine();
				String[] xystr=string.split(" ");
				Point point=new Point(i,Integer.valueOf(xystr[0]),Integer.valueOf(xystr[1]));
				maps.put(i, point);
			}
			bufferedReader.close();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	void createMatrix() {
		for(int i=0;i<vector;i++) {
			for(int j=0;j<vector;j++) {
				matrix[i][j]=Point.distance(maps.get(i), maps.get(j));
			}
		}
	}
	
/*	int[] geneticAlgorithm() {
		
	}*/
	void createArray(int a) {
		Random random=new Random();
		for(int i=0;i<sampleNum;i++) {
		for(int j=0;j<vector;j++) {
			array[a][i][j]=j;
		}
		for(int j=0;j<vector;j++) {
			int t=Math.abs(random.nextInt())%vector;
			int temp=array[a][i][j];
			array[a][i][j]=array[a][i][t];
			array[a][i][t]=temp;
		}
		}
	}
	
	void createTimeArray(int a) {
		for(int i=0;i<sampleNum;i++) {
			for(int j=0;j<vector-1;j++) {
				timeArray[a][i]=0;
			}
		}
		for(int i=0;i<sampleNum;i++) {
			for(int j=0;j<vector-1;j++) {
				timeArray[a][i]+=Point.distance(maps.get(array[a][i][j]), maps.get(array[a][i][j+1]));
			}
			timeArray[a][i]+=Point.distance(maps.get(array[a][i][0]), maps.get(array[a][i][vector-1]));
		}
	}
	
	void quickSort(int a,int left,int right) {
		if(left+1>=right)
			return;
		int leftSymbol=left;
		int rightSymbol=right-1;
		while(leftSymbol<rightSymbol) {
			while(timeArray[a][rightSymbol]>=timeArray[a][left]&&rightSymbol>leftSymbol) {
				rightSymbol--;
			}
			while(timeArray[a][leftSymbol]<=timeArray[a][left]&&rightSymbol>leftSymbol) {
				leftSymbol++;
			}
			if(rightSymbol>leftSymbol) {
				double temp=timeArray[a][leftSymbol];
				timeArray[a][leftSymbol]=timeArray[a][rightSymbol];
				timeArray[a][rightSymbol]=temp;
				exchangeArray(array[a][rightSymbol], array[a][leftSymbol]);
			}
		}
		double temp=timeArray[a][left];
		timeArray[a][left]=timeArray[a][leftSymbol];
		timeArray[a][leftSymbol]=temp;
		exchangeArray(array[a][left], array[a][leftSymbol]);
		quickSort(a,left, leftSymbol+1);
		quickSort(a,leftSymbol+1, right);
	}
	
	static void exchangeArray(int[] a,int[] b) {
		for(int i=0;i<vector;i++) {
			int temp=a[i];
			a[i]=b[i];
			b[i]=temp;
		}
	}
	
	void select(int a) {
		double sum=0;
		double[] proArray=new double[sampleNum];
		for(int i=0;i<sampleNum;i++) {
			for(int j=0;j<vector;j++) {
				temparray[a][i][j]=array[a][i][j];
			}
		}
		for(int i=0;i<sampleNum;i++) {
			sum+=1/timeArray[a][i]/timeArray[a][i];
		}
		proArray[0]=1/timeArray[a][0]/sum;
		for(int i=1;i<sampleNum;i++) {
			proArray[i]=1/timeArray[a][i]/timeArray[a][i]/sum+proArray[i-1];
		}
		double singleNum=1.0/sampleNum;
//		System.out.println(singleNum);
		int j=0,i=0;
		for(;i<sampleNum;i++) {
			for(;j<sampleNum;j++) {
				if(singleNum*j<proArray[i]) {
					for(int k=0;k<vector;k++) {
						array[a][j][k]=temparray[a][i][k];
					}
				}
				else {
					break;
				}
			}
		}
	}
	
	void crossHeredity(int[] a,int[] b) {
		int[] aSymbol=new int[vector];
		int[] bSymbol=new int[vector];
		for(int i=0;i<vector;i++) {
			aSymbol[i]=-1;
		}
		for(int i=0;i<vector;i++) {
			bSymbol[i]=-1;
		}
		Random random=new Random();
		int p=Math.abs(random.nextInt())%(vector);
		int q=p+Math.abs(random.nextInt())%(vector-p);
		int aNum=0,bNum;
		for(int i=p;i<q;i++) {
			int temp=a[i];
			a[i]=b[i];
			b[i]=temp;
		}
		for(int i=p;i<q;i++) {
			aSymbol[a[i]]=b[i];
		}
		aNum=q-p;
		for(int i=p;i<q;i++) {
			bSymbol[b[i]]=a[i];
		}
		bNum=q-p;
/*		for(int i=0;i<a.length;i++) {
			System.out.println(a[i]+" "+b[i]);
		}*/
		boolean[] aS=new boolean[vector];
		boolean[] bS=new boolean[vector];
		while(aNum<vector) {
			for(int i=0;i<p;i++) {
				if(aSymbol[a[i]]==-1) {
					aSymbol[a[i]]=b[i];
					aS[i]=true;
					aNum++;
				}
				else {
					if(aS[i]==false)
					a[i]=aSymbol[a[i]];
				}
			}
/*			for(int i=0;i<a.length;i++) {
				System.out.println(aSymbol[i]);
			}
			System.out.println();*/
			for(int i=q;i<vector;i++) {
				if(aSymbol[a[i]]==-1) {
					aSymbol[a[i]]=b[i];
					aS[i]=true;
					aNum++;
				}
				else {
					if(aS[i]==false)
					a[i]=aSymbol[a[i]];
				}
			}
		}
		while(bNum<vector) {
			for(int i=0;i<p;i++) {
				if(bSymbol[b[i]]==-1) {
					bSymbol[b[i]]=a[i];
					bS[i]=true;
					bNum++;
				}
				else {
					if(bS[i]==false)
					b[i]=bSymbol[b[i]];
				}
			}
			for(int i=q;i<vector;i++) {
				if(bSymbol[b[i]]==-1) {
					bSymbol[b[i]]=a[i];
					bS[i]=true;
					bNum++;
				}
				else {
					if(bS[i]==false)
					b[i]=bSymbol[b[i]];
				}
			}
		}
	}
	
	public void onCVariation(int[] a) {  
        int ran1, ran2, temp;  
        int count;
        Random random=new Random();
        count = Math.abs(random.nextInt()) % vector;  
  
        for (int i = 0; i < count; i++) {  
  
            ran1 = Math.abs(random.nextInt()) % vector;  
            ran2 = Math.abs(random.nextInt()) % vector;  
            while (ran1 == ran2) {  
                ran2 = Math.abs(random.nextInt()) % vector;  
            }  
            temp = a[ran1];  
            a[ran1] = a[ran2];  
            a[ran2] = temp;  
        }  
    }
	public void reproduce(int a) {
		quickSort(a,0, sampleNum);
//		select();
		Random random=new Random();
		double r;
		int k;
		for(int i=0;i<vector;i++) {
			insence[a][i]=array[a][0][i];
//			System.out.print(insence[i]+" ");
		}
		for(k=1;k<sampleNum-2;k=k+2) {
			r=Math.abs(random.nextDouble());
			if(r<pc) {
				crossHeredity(array[a][k], array[a][k+1]);
			}else {
				r=Math.abs(random.nextDouble());
				if(r<pm) {
					onCVariation(array[a][k]);
				}
				r=Math.abs(random.nextDouble());
				if(r<pm) {
					onCVariation(array[a][k+1]);
				}
			}
		}
		for(int i=0;i<vector;i++) {
			array[a][vector-1][i]=insence[a][i];
		}
/*		if(k==sampleNum-1) {
			r=Math.abs(random.nextDouble());
			if(r<pm) {
				onCVariation(array[k]);
			}
		}*/
	}

	 static void recombination(int a,int[] x) {
		 for(int i=0;i<vector;i++) {
			 if(a==x[i]) {
				 int j;
				 for(j=0;j<i;j++) {
					 tempArray[j]=x[j];
				 }
				 for(j=0;j<vector-i;j++) {
					 x[j]=x[i+j];
				 }
				 for(int k=0;j<vector;j++,k++) {
					 x[j]=tempArray[k];
				 } 
			 }
		 }
	 }
	 static int LCS(int[] a,int[] b) {
		 int goali=0;
		 int goalj=0;
		 int max=0;
		 for(int i=0;i<vector;i++) {
			 for(int j=0;j<vector;j++) {
				 if(a[i]==b[j]) {
					 LCSMatrix[i+1][j+1]=LCSMatrix[i][j]+1;
				 }
				 else {
					 LCSMatrix[i+1][j+1]=0;
				 }
				 if(LCSMatrix[i+1][j+1]>max) {
					 max=LCSMatrix[i+1][j+1];
					 goali=i;
					 goalj=j;
				 }
			 }
		 }
		 for(int i=goali,j=goalj;i>0;i--,j--) {
			 if(LCSMatrix[i][j]==1) {
				 return a[i-1];
			 }
		 }
		 return 0;
	 }
}

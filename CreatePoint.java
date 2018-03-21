import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class CreatePoint {

	public static final int range=800;
	public static final int vector=25;
	public static void main(String[] args) {
		Random random=new Random();
		File file=new File("Points.txt");
		try {
			FileWriter fileWriter = new FileWriter(file); 
			for(int i=0;i<vector;i++) {
				fileWriter.write(Math.abs(random.nextInt())%range+" "+Math.abs(random.nextInt())%range+"\n");
			}
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

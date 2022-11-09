import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Tester {
	public static void main(String args[]) {
		Calendar inst = Calendar.getInstance();
		int hour = inst.get(Calendar.HOUR_OF_DAY);
		int minute = inst.get(Calendar.MINUTE);
		
		int date = (Calendar.getInstance().get(Calendar.YEAR) * 10000) + (Calendar.getInstance().get(Calendar.MONTH) * 100) + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

		int min = minute - (minute % 15);
		int time = (hour * 100) + min;
		System.out.println(date);
	}
}

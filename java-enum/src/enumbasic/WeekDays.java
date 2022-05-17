package enumbasic;

enum Day{ SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY}  
public class WeekDays {
	
	public static void main(String[] args) {
		System.out.println(Day.SUNDAY);
		System.out.println(Day.SUNDAY.name());
		System.out.println(Day.SUNDAY.ordinal());
		System.out.println(Day.SUNDAY.values());
		
	}

}

package de.checkpoint.utils;

import java.util.Date;
import java.io.BufferedWriter;

public class TestCalenderInThread extends Thread
{
	int number = 0;
	public static UtlDateTimeConverter conv = UtlDateTimeConverter.converter();
	public static String outputPath = "log/treadOutput.txt";
	public static BufferedWriter out = null;

	public TestCalenderInThread(int i)
	{
		super();
		number = i;
	}

	public void run()
	{
		try {
			String outStr = "";
			for(; ; ) {
				try {
					sleep(10);
					int day = conv.getWeekDay(new Date());
					out.write("thread = " + number + "; " + +Thread.currentThread().getId() +
						" day = " + day + "\n");
					sleep(3);
					day = conv.getWeekDay(new Date(System.currentTimeMillis() - 10000000));
					out.write("thread = " + number + "; " + +Thread.currentThread().getId() +
						" day1 = " + day + "\n");
					sleep(11);
					day = conv.getWeekDay(new Date(System.currentTimeMillis() + 20000000));
					out.write("thread = " + number + "; " + +Thread.currentThread().getId() +
						" day2 = " + day + "\n");
					sleep(18);
					int year = conv.getYear(new Date());
					out.write("thread = " + number + "; " + +Thread.currentThread().getId() +
						" year = " + year + "\n");
				} catch(InterruptedException ex) {
					ex.printStackTrace();
				} catch(Exception e) {
					e.printStackTrace();
				}
				out.flush();
			}
		} catch(Exception ee) {
			ee.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		try {
			out = new java.io.BufferedWriter(new
				  java.io.FileWriter(outputPath));
			for(int i = 0; i < 10; i++) {
				TestThread1 thread1 = new TestThread1(i);
				thread1.start();
				try {
					Thread.sleep(7);
				} catch(InterruptedException ex) {
					ex.printStackTrace();
				}
				TestCalenderInThread thread = new TestCalenderInThread(i);
				thread.start();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	static class TestThread1 extends Thread
	{
		int number;
		public TestThread1(int i)
		{
			super();
			number = i;
		}

		public void run()
		{
			try {
				for(;;){
					sleep(23);
					Date date = conv.setTime(null, new Date(System.currentTimeMillis() + 10000000));
					out.write("TestThread1 = " + number + "; " + +Thread.currentThread().getId() +
						" setTime: Date = " + date.toString() + "\n");
					sleep(32);
					String str = conv.convertDateToStringEnglDetail(new java.util.Date(System.
								 currentTimeMillis()), false);
					out.write("TestThread1 = " + number + "; " + +Thread.currentThread().getId() +
						" convertDateToStringEnglDetail: Date = " + str + "\n");

					out.flush();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}

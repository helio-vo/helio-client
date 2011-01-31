package eu.heliovo.mockclient.core;

import java.util.Scanner;

import eu.heliovo.clientapi.core.HelioCore;

public class MockHelioCoreImpl implements HelioCore {

	public void login() {
		String name;
		int age;
		Scanner in = new Scanner(System.in);

		// Reads a single line from the console
		// and stores into name variable
		name = in.nextLine();

		// Reads a integer from the console
		// and stores into age variable
		age = in.nextInt();
		in.close();

		// Prints name and age to the console
		System.out.println("Name :" + name);
		System.out.println("Age :" + age);
		System.out.print("Password [pw]: ");
		for (int i = 0; i < 8; i++) {
			System.out.print("*");
			try {
				System.out.flush();
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		System.out.println("");
	}

	@Override
	public void login(String username, String password) {
		login();
	}
}

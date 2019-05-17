package me.flail.slashplayer.executables;

import me.flail.slashplayer.executables.Executables.Exe;
import me.flail.slashplayer.user.User;

public class Executioner {
	private User subject;
	private User operator;
	private Exe executable;

	public Executioner(User subject, Exe executable) {
		this.subject = subject;
		this.executable = executable;
	}

	public boolean execute(User operator) {
		this.operator = operator;

		return execute(executable, subject, this.operator);
	}

	private boolean execute(Exe exe, User subject, User operator) {

		return false;
	}

}

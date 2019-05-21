package me.flail.slashplayer.executables;

import me.flail.slashplayer.executables.Executables.Exe;
import me.flail.slashplayer.tools.Logger;
import me.flail.slashplayer.user.User;

public class Executioner extends Logger {
	private User subject;
	private User operator;
	private Exe executable;

	/**
	 * @param subject
	 *                       the poor unfortunate of this calamity
	 * @param executable
	 *                       whatever calamity to make befall this sorry subject
	 */
	public Executioner(User subject, Exe executable) {
		this.subject = subject;
		this.executable = executable;
	}

	/**
	 * @param operator
	 *                     the glorified butcher
	 * @return true if operation was sucessful
	 */
	public boolean execute(User operator) {
		this.operator = operator;

		return execute(executable, subject, this.operator);
	}

	private boolean execute(Exe exe, User subject, User operator) {
		if (subject.isOnline() && operator.isOnline()) {


			return true;
		}

		if (exe.equals(Exe.WHITELIST)) {

		}

		return false;
	}

}

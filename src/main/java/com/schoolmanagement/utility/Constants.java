package com.schoolmanagement.utility;

public class Constants {

	public enum UserRole {
		ROLE_STUDENT("Student"), ROLE_ADMIN("Admin"), ROLE_TEACHER("Teacher");

		private String role;

		private UserRole(String role) {
			this.role = role;
		}

		public String value() {
			return this.role;
		}
	}

	public enum ActiveStatus {
		ACTIVE("Active"), DEACTIVATED("Deactivated");

		private String status;

		private ActiveStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}
	
	public enum DayOfWeekEnum {
	    MONDAY("Monday"),
	    TUESDAY("Tuesday"),
	    WEDNESDAY("Wednesday"),
	    THURSDAY("Thursday"),
	    FRIDAY("Friday"),
	    SATURDAY("Saturday"),
	    SUNDAY("Sunday");

	    private final String day;

	    private DayOfWeekEnum(String day) {
	        this.day = day;
	    }

	    public String value() {
	        return day;
	    }
	}
	
	public enum TimeSlotEnum {
		 SLOT_9AM_10AM("9:00 AM - 10:00 AM"),
		 SLOT_10AM_11AM("10:00 AM - 11:00 AM"),
		 SLOT_11AM_12PM("11:00 AM - 12:00 PM"),
		 SLOT_12PM_1PM("12:00 PM - 1:00 PM"),
		 SLOT_1PM_2PM("1:00 PM - 2:00 PM"),
		 SLOT_2PM_3PM("2:00 PM - 3:00 PM"),
		 SLOT_3PM_4PM("3:00 PM - 4:00 PM"),
		 SLOT_4PM_5PM("4:00 PM - 5:00 PM");

	    private final String slot;

	    private TimeSlotEnum(String slot) {
	        this.slot = slot;
	    }

	    public String value() {
	        return slot;
	    }
	}
	
	public enum IsShowChangePasswordPage {
		YES(0), NO(1);

		private int show;

		private IsShowChangePasswordPage(int show) {
			this.show = show;
		}

		public int value() {
			return this.show;
		}
	}
	
	public enum ExamSubmissionMessage {
	    CONGRATULATIONS_PASS("Congratulations! You have passed the exam. Well done!"),
	    FAILED("Unfortunately, you did not pass the exam."),
	    SUBMITTED("Submitted");

	    private final String message;

	    ExamSubmissionMessage(String message) {
	        this.message = message;
	    }

	    public String value() {
	        return message;
	    }
	}
	
	public enum ExamResultStatus {
	    PASS("Pass"),
	    FAIL("Fail");

	    private final String status;

	    ExamResultStatus(String status) {
	        this.status = status;
	    }

	    public String value() {
	        return status;
	    }
	}

	public enum WorkingStatus {
		WORKING("Working"), 
		HOLIDAY("Holiday");

		private String status;

		private WorkingStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}
	
	public enum AttendanceStatus {
		 // no deduction in pay slip
		PRESENT("Present"), 
		HALF_DAY_PRESENT("Half Day Present"),  
		ABSENT("Absent");
		

		private String status;

		private AttendanceStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}
	
	public enum LeaveRequestStatus {
		APPROVED("Approved"), 
		REJECTED("Rejected"),
		PENDING("Pending");

		private String status;

		private LeaveRequestStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}
	
	public enum AssignmentSubmissionStatus {
	    
	    SUBMITTED("Submitted"),
	    PENDING("Pending"),
	    CHECKED("Checked"),
	    NOT_SUBMITTED("Not Submitted");

	    private final String status;

	    private AssignmentSubmissionStatus(String status) {
	        this.status = status;
	    }

	    public String value() {
	        return this.status;
	    }
	}
	
    public enum AssignmentStatus {
	    
	    ACTIVE("Active"),
	    DEACTIVATED("Deactivated"),  // this will effect the grade
	    DEADLINE("Deadline");   // submission date over, this will effect the grade

	    private final String status;

	    private AssignmentStatus(String status) {
	        this.status = status;
	    }

	    public String value() {
	        return this.status;
	    }
	}
    
    public enum AssignmentGrade {

        A("A", "Excellent"),
        B("B", "Good"),
        C("C", "Average"),
        D("D", "Below Average"),
        E("E", "Poor"),
        F("F", "Fail");

        private final String code;
        private final String description;

        private AssignmentGrade(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        // Optional: for displaying both together
        @Override
        public String toString() {
            return code + " - " + description;
        }
    }



}

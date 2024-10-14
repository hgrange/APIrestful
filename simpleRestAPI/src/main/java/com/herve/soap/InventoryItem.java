package com.herve.soap;

import java.util.Date;



public class InventoryItem {
	  private int Id;
	    private String Name;
	    private Date joinDate;
		public InventoryItem(int id2, String name2, Date date) {
			//TODO Auto-generated constructor stub
			this.setId(id2);
			this.setName(name2);
			this.setJoinDate(date);
		}
		public int getId() {
			return Id;
		}
		public void setId(int id) {
			Id = id;
		}
		public String getName() {
			return Name;
		}
		public void setName(String name) {
			Name = name;
		}
		public Date getJoinDate() {
			return joinDate;
		}
		public void setJoinDate(Date joinDate) {
			this.joinDate = joinDate;
		}
}



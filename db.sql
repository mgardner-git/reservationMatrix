DROP TABLE IF EXISTS reservable_reservation;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS reservable;


CREATE TABLE reservable(
 id int(10) PRIMARY KEY NOT NULL AUTO_INCREMENT,
 name VARCHAR(20) NOT NULL 
);
 
CREATE TABLE reservation(
	id int(10) PRIMARY KEY NOT NULL AUTO_INCREMENT,
	beginDate DATE NOT NULL,
	endDate DATE NOT NULL
);
CREATE TABLE reservable_reservation( 	
	id int(10) PRIMARY KEY NOT NULL AUTO_INCREMENT,
	fk_reservation_id int(10) NOT NULL,
	fk_reservable_id int(10) NOT NULL,
	FOREIGN KEY (fk_reservation_id) REFERENCES reservation(id),
	FOREIGN KEY (fk_reservable_id) REFERENCES reservable(id)
);

INSERT INTO reservable (id,name) VALUES ("1","A");
INSERT INTO reservable (id,name) VALUES ("2","B");
INSERT INTO reservable (id,name) VALUES ("3","C");
INSERT INTO reservable (id,name) VALUES ("4","D");

INSERT INTO reservation (id, beginDate,endDate) VALUES (1, STR_TO_DATE("7/1/2015","%m/%d/%Y"), STR_TO_DATE("7/30/2015", "%m/%d/%Y"));
INSERT INTO reservable_reservation(id, fk_reservation_id, fk_reservable_id) VALUES (1,1,1);
INSERT INTO reservable_reservation(id, fk_reservation_id, fk_reservable_id) VALUES (2,1,2);
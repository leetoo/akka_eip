//https://www.datastax.com/dev/blog/the-most-important-thing-to-know-in-cassandra-data-modeling-the-primary-key

CREATE keyspace akka_eip with replication={'class':'SimpleStrategy','replication_factor':1};
CREATE TABLE IF NOT EXISTS akka_eip.cctable (ccid text,id text,manifest text,msg blob,PRIMARY KEY (ccid, id)) WITH default_time_to_live = 3000;
CREATE TABLE IF NOT EXISTS akka_eip.users(id text, name text, email text, PRIMARY Key(id)) ;
CREATE TABLE IF NOT EXISTS akka_eip.userscc(ccid text, id text, name text, email text, PRIMARY Key(ccid, id)) ;
INSERT INTO akka_eip.userscc(1,1,'michal','lolo@wp.pl');
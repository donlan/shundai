CREATE TABLE IF NOT EXISTS category
(
	Id INTEGER AUTO_INCREMENT PRIMARY KEY,
	customField	TEXT,
    	_id	TEXT UNIQUE,
    	createTime	TEXT,
    	name	TEXT,
    	nextPageUrl	TEXT,
    	iconUrl TEXT
);


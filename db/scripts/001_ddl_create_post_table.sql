create table post (
      Id SERIAL PRIMARY KEY,
      name TEXT NOT NULL,
      text TEXT NOT NULL,
      link TEXT UNIQUE NOT NULL,
      created TIMESTAMP NOT NULL
);
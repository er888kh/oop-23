CREATE MIGRATION m1bz4cp544t7yhg36e2rsdtn7c4clwx7u75vbuiidadt5bacuklesa
    ONTO initial
{
  CREATE TYPE default::Comment {
      CREATE REQUIRED PROPERTY text: std::str;
  };
  CREATE TYPE default::Restaurant {
      CREATE REQUIRED PROPERTY map_node: std::int64;
      CREATE REQUIRED PROPERTY name: std::str;
  };
  ALTER TYPE default::Comment {
      CREATE REQUIRED LINK restaurant: default::Restaurant;
  };
  ALTER TYPE default::Restaurant {
      CREATE MULTI LINK comments := (.<restaurant[IS default::Comment]);
  };
  CREATE TYPE default::User {
      CREATE REQUIRED PROPERTY firstname: std::str;
      CREATE REQUIRED PROPERTY lastname: std::str;
      CREATE PROPERTY fullname := (((.firstname ++ ' ') ++ .lastname));
      CREATE REQUIRED PROPERTY is_admin: std::bool;
      CREATE REQUIRED PROPERTY password_hash: std::str {
          CREATE CONSTRAINT std::min_len_value(32);
      };
      CREATE REQUIRED PROPERTY salt: std::str {
          CREATE CONSTRAINT std::exclusive;
          CREATE CONSTRAINT std::min_len_value(8);
      };
      CREATE REQUIRED PROPERTY username: std::str {
          CREATE CONSTRAINT std::exclusive;
      };
  };
  ALTER TYPE default::Comment {
      CREATE REQUIRED LINK user: default::User;
  };
  ALTER TYPE default::User {
      CREATE MULTI LINK comments := (.<user[IS default::Comment]);
  };
  CREATE TYPE default::CustomerOrder {
      CREATE REQUIRED LINK user: default::User;
      CREATE REQUIRED PROPERTY created_at: std::datetime {
          SET default := (std::datetime_of_statement());
          SET readonly := true;
      };
      CREATE REQUIRED PROPERTY delivered: std::bool {
          SET default := false;
      };
      CREATE REQUIRED PROPERTY map_node: std::int64;
      CREATE REQUIRED PROPERTY price: std::float64;
  };
  CREATE TYPE default::Food {
      CREATE REQUIRED PROPERTY active: std::bool {
          SET default := true;
      };
      CREATE REQUIRED PROPERTY sale_value: std::float64 {
          SET default := 1.0;
      };
      CREATE REQUIRED PROPERTY name: std::str;
      CREATE REQUIRED PROPERTY price: std::float64;
  };
  ALTER TYPE default::CustomerOrder {
      CREATE REQUIRED LINK food: default::Food;
  };
  ALTER TYPE default::User {
      CREATE MULTI LINK orders := (.<user[IS default::CustomerOrder]);
  };
  ALTER TYPE default::Restaurant {
      CREATE MULTI LINK menu: default::Food {
          CREATE CONSTRAINT std::exclusive;
      };
      CREATE MULTI LINK active_menu := (SELECT
          .menu
      FILTER
          (.active = true)
      );
      CREATE MULTI LINK sale_menu := (SELECT
          .active_menu
      FILTER
          (.sale_value != 1.0)
      );
      CREATE REQUIRED LINK manager: default::User;
  };
  ALTER TYPE default::Food {
      CREATE SINGLE LINK restaurant := (.<menu[IS default::Restaurant]);
  };
  CREATE TYPE default::Rating {
      CREATE REQUIRED LINK food: default::Food;
      CREATE REQUIRED LINK user: default::User;
      CREATE REQUIRED PROPERTY rating: std::float64;
  };
  ALTER TYPE default::User {
      CREATE MULTI LINK ratings := (.<user[IS default::Rating]);
  };
};

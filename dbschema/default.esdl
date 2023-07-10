module default {
    type User {
        required username: str {
            constraint exclusive;
        }
        required password_hash: str {
            constraint min_len_value(32);
        }
        required salt: str {
            constraint min_len_value(8);
            constraint exclusive;
        }
        required firstname: str;
        required lastname: str;
        required is_admin: bool;
        required balance: float64;

        property fullname := .firstname ++ ' ' ++ .lastname;
        multi link ratings := .<user[is Rating];
        multi link comments := .<user[is Comment];
        multi link orders := .<user[is CustomerOrder];
    }

    type Rating {
        required food: Food {
            on target delete delete source;
	}
        required rating: float64;
        required user: User;
    }

    type Comment {
        required restaurant: Restaurant;
        required user: User;
        required text: str;
        optional response: str;
    }

    type Restaurant {
        required name: str;
        required map_node: int64;
        required manager: User;

        multi menu: Food {
            constraint exclusive;
            on target delete allow;
        }

        multi link active_menu := (
            select .menu filter .active = true
        );
        multi link sale_menu := (
            select .active_menu filter .sale_value != 1.0
        );
        multi link comments := .<restaurant[is Comment];
    }

    type Food {
        required name: str;
        required price: float64;
        required sale_value: float64 {
            default := 1.0;
        }
        required active: bool {
            default := true;
        }

        single link restaurant := .<menu[is Restaurant];
        multi link ratings := .<food[is Rating];
    }

    type CustomerOrder {
        required order_id: uuid;
        required user: User;
        required restaurant: Restaurant;
        required map_node: int64;
        required price: float64;
        required delivered: bool {
            default := false;
        }

        required multi food: Food;

        required created_at: datetime {
            readonly := true;
            default := datetime_of_statement();
        }
    }
}

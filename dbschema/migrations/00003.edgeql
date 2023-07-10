CREATE MIGRATION m1hqhbuqcyow4gppoxybist6cwlnshejq7l72pc3a6oz4j5fw6bh2q
    ONTO m1s43csdmm4gol6tbo2c2k7g776guzzrzex4fcg2p2a54m3ubp2r6a
{
  ALTER TYPE default::CustomerOrder {
      ALTER LINK food {
          SET MULTI;
      };
      CREATE REQUIRED PROPERTY order_id: std::uuid {
          SET REQUIRED USING (<std::uuid>{});
      };
  };
  ALTER TYPE default::User {
      CREATE REQUIRED PROPERTY balance: std::float64 {
          SET REQUIRED USING (<std::float64>{});
      };
  };
};

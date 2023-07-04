CREATE MIGRATION m1s43csdmm4gol6tbo2c2k7g776guzzrzex4fcg2p2a54m3ubp2r6a
    ONTO m1bz4cp544t7yhg36e2rsdtn7c4clwx7u75vbuiidadt5bacuklesa
{
  ALTER TYPE default::Comment {
      CREATE OPTIONAL PROPERTY response: std::str;
  };
};

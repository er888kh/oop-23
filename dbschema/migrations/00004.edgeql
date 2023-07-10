CREATE MIGRATION m1vvr4ok67x2bgkyqiykg2s5c6dxeaiiilfvzbmy2zwc44wqfghuaq
    ONTO m1hqhbuqcyow4gppoxybist6cwlnshejq7l72pc3a6oz4j5fw6bh2q
{
  ALTER TYPE default::Rating {
      ALTER LINK food {
          ON TARGET DELETE DELETE SOURCE;
      };
  };
  ALTER TYPE default::Restaurant {
      ALTER LINK menu {
          ON TARGET DELETE ALLOW;
      };
  };
};

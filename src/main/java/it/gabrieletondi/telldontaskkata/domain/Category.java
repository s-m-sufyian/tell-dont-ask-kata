package it.gabrieletondi.telldontaskkata.domain;

import java.math.BigDecimal;

public class Category {

  private String name;
  private BigDecimal taxPercentage;

  public Category(String name, BigDecimal taxPercentage) {
    this.name = name;
    this.taxPercentage = taxPercentage;
  }

  BigDecimal getTaxPercentage() {
    return taxPercentage;
  }
}
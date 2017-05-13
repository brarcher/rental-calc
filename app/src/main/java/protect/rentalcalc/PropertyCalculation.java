package protect.rentalcalc;

public class PropertyCalculation
{
    // INCOME:

    // Takes into account income increase
    double grossRent;

    // percentage of gross rent
    double vacancy;

    // gross rent - vacancy
    double operatingIncome;

    // EXPENSES:

    // Takes into account expense increase
    double totalExpenses;

    // operating income - total expenses
    double netOperatingIncome;

    // CASH FLOW:

    // Only for the years that the loan is being paid off
    double loanPayments;

    // net operating income - loan payments
    double cashFlow;

    // cash flow after taxes
    double afterTaxCashFlow;

    // EQUITY:

    // Take into account appreciation
    double propertyValue;

    // Amount left to pay off
    double loanBalance;

    // property value - loan balance
    double totalEquity;

    // TAX BENEFITS:

    // In the US, only for the first 27.5 years of ownership.
    // Takes into account the (purchase price - land value + purchase costs) / 27.5
    double depreciation;

    // The amount of interest paid on the loan
    double loanInterest;

    // RETURNS:

    double capitalization;

    double cashOnCash;

    double rentToValue;

    double grossRentMultiplier;
}

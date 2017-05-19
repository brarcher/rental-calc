package protect.rentalcalc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CalcUtil
{
    static List<PropertyCalculation> calculateForYears(Property property, int numYears)
    {
        List<PropertyCalculation> list = new ArrayList<>(numYears);

        double grossRent = property.grossRent * 12;
        double totalExpenses = grossRent * property.expenses / 100.0;
        if(property.expensesItemized.isEmpty() == false)
        {
            totalExpenses = CalcUtil.sumMapItems(property.expensesItemized) * 12;
        }

        double downPercent;
        if(property.useLoan)
        {
            downPercent = ((double)property.downPayment)/100.0;
        }
        else
        {
            downPercent = 1.0;
        }

        double mortgage = monthlyMortgagePayment(property);
        double yearlyMortgage = mortgage*12;
        double downPayment = downPercent * (double)property.purchasePrice;

        double loanBalance = property.purchasePrice - downPayment;

        double propertyValue = property.afterRepairsValue;

        double purchaseCost = property.purchaseCosts*property.purchasePrice/100.0;
        if(property.purchaseCostsItemized.isEmpty() == false)
        {
            purchaseCost = CalcUtil.sumMapItems(property.purchaseCostsItemized);
        }

        double depreciation = (property.purchasePrice - property.landValue + purchaseCost) / 27.5;

        int repairRemodelCosts = property.repairRemodelCosts;
        if(property.repairRemodelCostsItemized.isEmpty() == false)
        {
            repairRemodelCosts = CalcUtil.sumMapItems(property.repairRemodelCostsItemized);
        }

        double totalCashNeeded = downPayment + purchaseCost + repairRemodelCosts;

        for(int year = 1; year <= numYears; year++)
        {
            PropertyCalculation calc = new PropertyCalculation();

            calc.grossRent = grossRent;
            grossRent *= (1 + property.incomeIncrease/100.0);

            calc.vacancy = calc.grossRent * property.vacancy / 100.0;

            calc.operatingIncome = calc.grossRent - calc.vacancy;

            calc.totalExpenses = totalExpenses;
            totalExpenses *= (1 + property.expenseIncrease/100.0);

            calc.netOperatingIncome = calc.operatingIncome - calc.totalExpenses;

            calc.loanPayments = yearlyMortgage;
            if(calc.loanPayments > loanBalance)
            {
                calc.loanPayments = loanBalance;
            }

            calc.cashFlow = calc.netOperatingIncome - calc.loanPayments;
            calc.afterTaxCashFlow = calc.cashFlow * (1 - property.incomeTaxRate/100.0);

            calc.propertyValue = propertyValue;
            propertyValue *= (1 + property.appreciation/100.0);

            for(int month = 1; month <= 12; month++)
            {
                double interest = loanBalance * property.interestRate/100.0/12;
                double principal = mortgage - interest;

                calc.loanInterest += interest;
                loanBalance -= principal;
                if(loanBalance < 0)
                {
                    loanBalance = 0;
                }
            }

            calc.loanBalance = loanBalance;
            calc.totalEquity = calc.propertyValue - loanBalance;

            if(year <= 27)
            {
                calc.depreciation = depreciation;
            }
            else if(year == 28)
            {
                // On the last year, one only receives 1/2 of the normal value
                calc.depreciation = depreciation/2;
            }

            if(property.purchasePrice > 0)
            {
                calc.capitalization = calc.netOperatingIncome * 100.0 / (double)property.purchasePrice;
            }

            if(totalCashNeeded > 0)
            {
                calc.cashOnCash = calc.cashFlow * 100.0 / totalCashNeeded;
            }

            if(property.purchasePrice > 0)
            {
                calc.rentToValue = calc.grossRent / 12.0 * 100.0 / propertyValue;
            }

            if(property.grossRent > 0)
            {
                calc.grossRentMultiplier = (double)property.purchasePrice / calc.grossRent;
            }

            list.add(year-1, calc);
        }

        return list;
    }

    static double monthlyMortgagePayment(Property property)
    {
        double downPercent;
        if(property.useLoan)
        {
            downPercent = ((double)property.downPayment)/100.0;
        }
        else
        {
            downPercent = 1.0;
        }
        double financedPercent = 1.0 - downPercent;
        double financed = property.purchasePrice * financedPercent;
        double monthlyInterestRate = property.interestRate / 100.0 / 12;
        int paymentMonths = property.loanDuration * 12;
        double onePlusRateRaised = Math.pow(1 + monthlyInterestRate, paymentMonths);
        double mortgage = financed * (monthlyInterestRate * onePlusRateRaised) / (onePlusRateRaised - 1);

        return mortgage;
    }

    static int sumMapItems(Map<String, Integer> map)
    {
        int value = 0;

        for(Map.Entry<String, Integer> entry : map.entrySet())
        {
            value += entry.getValue();
        }

        return value;
    }
}

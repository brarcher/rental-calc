package protect.rentalcalc;

class DictionaryItem
{
    final Integer titleId;
    final Integer definitionId;
    final Integer formulaId;

    DictionaryItem(int title, int definition)
    {
        titleId = title;
        definitionId = definition;
        formulaId = null;
    }

    DictionaryItem(int title, int definition, int formula)
    {
        titleId = title;
        definitionId = definition;
        formulaId = formula;
    }
}

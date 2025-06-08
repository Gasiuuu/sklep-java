export class ProductsToOrder {
    constructor(productId, number) {
        this._productId = productId;
        this._number = number;
    }

    get productId() {
        return this._productId;
    }

    set productId(newProductId) {
        if (typeof newProductId === 'number') {
            this._productId = newProductId;
        } else {
            console.error('Nieprawidłowy typ danych dla productId');
        }
    }

    get number() {
        return this._number;
    }

    set number(newNumber) {
        if (typeof newNumber === 'number' && newNumber > 0) {
            this._number = newNumber;
        } else {
            console.error('Nieprawidłowy typ danych lub wartość dla number');
        }
    }

    toJSON() {
        return {
            productId: this._productId,
            productNumber: Number(this._number)
        };
    }

}
export default ProductsToOrder;

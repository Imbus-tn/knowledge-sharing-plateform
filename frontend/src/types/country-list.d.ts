declare module 'country-list' {
    export interface Country {
      name: string;
      code: string;
      phone: string;
      iso2: string;
    }
  
    const countries: {
      getNames(): string[];
      getCode(name: string): string | undefined;
      getData(): Country[];
    };
  
    export default countries;
  }
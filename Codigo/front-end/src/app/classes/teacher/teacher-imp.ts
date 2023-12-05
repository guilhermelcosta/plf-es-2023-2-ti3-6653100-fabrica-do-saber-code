import {Teacher} from '../../interfaces/Teacher';

export class TeacherImp implements Teacher {

  id: number;
  fullName: string;
  cpf: string;
  rg: string;
  email: string;
  phoneNumber: string;
  addressNumber: string;
  addressComplement: string;
  streetAddress: string;
  neighborhood: string;
  zipCode: string;
  cityOfResidence: string;
  homeState: string;
  registrationDate: string;
  birthDate: string;
  salary: number;
  hireDate: string;
  terminationDate: string;
  academicFormation: string;
  academicFormationStatus: string;

  constructor() {
    this.id = 0;
    this.fullName = '';
    this.cpf = '';
    this.rg = '';
    this.email = '';
    this.phoneNumber = '';
    this.addressNumber = '';
    this.addressComplement = '';
    this.streetAddress = '';
    this.neighborhood = '';
    this.zipCode = '';
    this.cityOfResidence = '';
    this.homeState = '';
    this.registrationDate = '';
    this.birthDate = '';
    this.salary = 0;
    this.hireDate = '';
    this.terminationDate = '';
    this.academicFormation = '';
    this.academicFormationStatus = '';
  }
}

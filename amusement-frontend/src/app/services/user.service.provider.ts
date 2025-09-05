import { Injectable } from '@angular/core';
import { UserService } from './user.service';
import { MockUserService } from './mock-user.service';

// Set this to true for development (mock service), false for production (real service)
const USE_MOCK_SERVICE = false;

@Injectable({
  providedIn: 'root'
})
export class UserServiceProvider {
  private userService: UserService | MockUserService;

  constructor(
    private realUserService: UserService,
    private mockUserService: MockUserService
  ) {
    this.userService = USE_MOCK_SERVICE ? this.mockUserService : this.realUserService;
  }

  get service(): UserService | MockUserService {
    return this.userService;
  }

  // Method to check if using mock service
  isUsingMockService(): boolean {
    return USE_MOCK_SERVICE;
  }
} 
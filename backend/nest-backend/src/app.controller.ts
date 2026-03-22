import { Controller, Get } from '@nestjs/common';

@Controller()
export class AppController {
  @Get()
  getHello(): string {
    return '🤖 AI Life Strategist Backend is Successfully Running! To test the API, try navigating to /logs or /reports.';
  }
}

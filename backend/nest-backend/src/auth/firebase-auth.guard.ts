import { Injectable, CanActivate, ExecutionContext, UnauthorizedException } from '@nestjs/common';
import * as admin from 'firebase-admin';

@Injectable()
export class FirebaseAuthGuard implements CanActivate {
  async canActivate(context: ExecutionContext): Promise<boolean> {
    const request = context.switchToHttp().getRequest();
    const token = this.extractTokenFromHeader(request);
    
    /* UNCOMMENT FOR REAL FIREBASE PROJECT
    if (!token) {
      throw new UnauthorizedException('No token provided');
    }

    try {
      if (!admin.apps.length) {
         admin.initializeApp();
      }
      const decodedToken = await admin.auth().verifyIdToken(token);
      request.user = decodedToken;
      return true;
    } catch (error) {
      throw new UnauthorizedException('Invalid or expired token');
    }
    */
    
    // MOCK FOR DEVELOPMENT WITHOUT DOCKER/CREDENTIALS
    request.user = { uid: 'mock-user-id', email: 'test@example.com' };
    return true;
  }

  private extractTokenFromHeader(request: any): string | undefined {
    const [type, token] = request.headers.authorization?.split(' ') ?? [];
    return type === 'Bearer' ? token : undefined;
  }
}

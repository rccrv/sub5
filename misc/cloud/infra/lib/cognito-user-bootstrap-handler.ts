import {
  AdminAddUserToGroupCommand,
  AdminCreateUserCommand,
  AdminDeleteUserCommand,
  AdminSetUserPasswordCommand,
  CognitoIdentityProviderClient,
  UserNotFoundException,
  UsernameExistsException
} from '@aws-sdk/client-cognito-identity-provider';

type UserDefinition = {
  username: string;
  group: string;
};

type CognitoUserProperties = {
  userPoolId: string;
  users: string;
};

const cognito = new CognitoIdentityProviderClient({});

function parseUsers(value: string): UserDefinition[] {
  const users = JSON.parse(value) as UserDefinition[];
  if (!Array.isArray(users) || users.some((user) => !user.username || !user.group)) {
    throw new Error('Cognito users must contain username and group values.');
  }
  return users;
}

export async function handler(event: {
  RequestType: 'Create' | 'Update' | 'Delete';
  PhysicalResourceId?: string;
  ResourceProperties: CognitoUserProperties;
}) {
  const { userPoolId } = event.ResourceProperties;
  const users = parseUsers(event.ResourceProperties.users);
  const physicalResourceId = event.PhysicalResourceId ?? `cars-galore-cognito-users-${userPoolId}`;

  if (event.RequestType === 'Delete') {
    for (const user of users) {
      try {
        await cognito.send(new AdminDeleteUserCommand({
          UserPoolId: userPoolId,
          Username: user.username
        }));
      } catch (error) {
        if (!(error instanceof UserNotFoundException)) {
          throw error;
        }
      }
    }
    return { PhysicalResourceId: physicalResourceId };
  }

  for (const user of users) {
    try {
      await cognito.send(new AdminCreateUserCommand({
        UserPoolId: userPoolId,
        Username: user.username,
        MessageAction: 'SUPPRESS'
      }));
    } catch (error) {
      if (!(error instanceof UsernameExistsException)) {
        throw error;
      }
    }

    await cognito.send(new AdminSetUserPasswordCommand({
      UserPoolId: userPoolId,
      Username: user.username,
      Password: user.username,
      Permanent: true
    }));
    await cognito.send(new AdminAddUserToGroupCommand({
      UserPoolId: userPoolId,
      Username: user.username,
      GroupName: user.group
    }));
  }

  return { PhysicalResourceId: physicalResourceId };
}

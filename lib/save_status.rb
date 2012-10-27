# Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

module SaveStatus
  def save_and_return_status(sync_id)
    return {:sync_id => sync_id, :status => :ok} if self.save
    return {:sync_id => sync_id, :status => :error, :errors => self.errors.full_messages}
  end
end
